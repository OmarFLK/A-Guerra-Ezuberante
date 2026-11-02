"""Native 64x64 pixel art. Explicit face maps, directional material ramps, no noise or downscaling.

Only writes the 24 PNGs behind the requested eight egg families and their documented source copies.
Gameplay, UV geometry, animation files and entity/item IDs remain untouched.
"""
import json, sys, shutil
from pathlib import Path
from PIL import Image, ImageDraw

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
sys.path.insert(0, str(HERE))
from auditar import OUT, TEXTURES, entries

# Steve boxes: canonical atlas origins, width, depth, height.
BOXES = {"head": (0, 0, 8, 8, 8), "body": (16, 16, 8, 4, 12), "right_arm": (40, 16, 4, 4, 12),
         "left_arm": (32, 48, 4, 4, 12), "right_leg": (0, 16, 4, 4, 12), "left_leg": (16, 48, 4, 4, 12)}
LIGHT = {"F": 1.0, "R": .85, "L": .95, "B": .88, "top": 1.05, "bottom": .72}

def rgb(value):
    return tuple(int(value[i:i+2], 16) for i in (0, 2, 4))

def tint(color, factor):
    return tuple(min(255, max(0, round(v * factor))) for v in color)

def ramp(color):
    c = rgb(color)
    return [tint(c, x) for x in (.64, .79, .94, 1.08, 1.20)]

SKIN = {
    "fair": ("a46f51", "c7906c", "dda77e", "e9bc97", "f2ceb0"),
    "warm": ("8c583f", "ae7553", "c58c65", "d9a57f", "e4bc98"),
    "bronze": ("754734", "945c40", "ad7753", "c2906a", "d3a780"),
    "brown": ("533529", "704735", "8b6046", "a37b59", "bb9470"),
    "deep": ("35251f", "50362b", "694a36", "825e45", "9c7756"),
}

HAIR = {
    "black": ("201c1c", "312827", "493a33"), "brown": ("352922", "514032", "705743"),
    "chestnut": ("4a3024", "6b4933", "91684b"), "grey": ("4e4945", "7e7971", "aca69a"),
    "salt": ("34322f", "62625b", "97958b"), "blond": ("756044", "a48a57", "cbb275"),
    "zema": ("4d4238", "766c5c", "a79e88"),
}

# Eight separately authored face compositions. Hairs/eyes/mouth are not pasted from an approved person's skin.
FACES = {
    "ciro": ["hijjjjih", "i223332i", "23344332", "2bb33bb2", "2we33ew2", "23345332", "224rr422", "12222221"],
    "zema": ["hijjiijh", "hi2332jh", "12344321", "12b33b21", "1we33ew1", "23455332", "123rr321", "11233211"],
    "boulos": ["hijhhjih", "hi2332ih", "2bb33bb2", "2we33ew2", "d234432d", "dvddddvd", "dvvrrvvd", "ddvvvvdd"],
    "eneas": ["h233332h", "12344321", "2gg33gg2", "gwegg ewg", "dv2552vd", "dvddddvd", "dvvrrvvd", "ddvvvvdd"],
    "comunista": ["hijhhijh", "hi2332ih", "1b2332b1", "1we33ew1", "23345332", "23444332", "12rrr321", "11233211"],
    "bolsonarista": ["hhijjihh", "hj2332ih", "12344321", "2bb33bb2", "2we33ew2", "23345332", "2vrrrrv2", "12vvvv21"],
    "direitista": ["hhijjihh", "hij2332h", "hi233332", "2b333bb2", "2we33ew2", "23354332", "123rr321", "11222211"],
    "petista": ["hihjjhih", "h233332h", "2bb33bb2", "2we33ew2", "12345321", "1v2332v1", "12vrrv21", "112vv211"],
}

def palette(skin, hair, fabric, pants, trim="d6d7cc", beard=None):
    p = {str(i+1): rgb(c) for i, c in enumerate(SKIN[skin])}
    p.update(dict(zip("hij", map(rgb, HAIR[hair]))))
    p.update(dict(zip("ABCDE", ramp(fabric))))
    p.update(dict(zip("KLMNO", ramp(pants))))
    beard = HAIR[beard or hair]
    p.update({"b": tint(rgb(beard[0]), 1.13), "d": rgb(beard[0]), "v": rgb(beard[1]), "u": rgb(beard[2]),
              "e": rgb("24272a"), "w": rgb("e5ddd0"), "r": rgb("956455"), "g": rgb("58534c"),
              "P": rgb(trim), "Q": tint(rgb(trim), .76), "S": rgb("181c23"), "T": rgb("353b43"),
              "U": rgb("545963"), "X": rgb("8b6945"), "Y": rgb("c8ad6a"), "Z": rgb("c7c5b8")})
    return p

class Skin:
    def __init__(self, colors):
        self.image = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
        self.colors = colors

    def face(self, part, side, rows):
        u, v, w, d, h = BOXES[part]
        rect = {"R": (u, v+d, d, h), "F": (u+d, v+d, w, h), "L": (u+d+w, v+d, d, h),
                "B": (u+2*d+w, v+d, w, h), "top": (u+d, v, w, d), "bottom": (u+d+w, v, w, d)}[side]
        x0, y0, width, height = rect
        rows = [r.replace(" ", "") for r in rows]
        assert len(rows) == height, (part, side, len(rows), height)
        for y, row in enumerate(rows):
            assert len(row) == width, (part, side, row, width)
            for x, symbol in enumerate(row):
                self.image.putpixel((x0+x, y0+y), (*tint(self.colors[symbol], LIGHT[side]), 255))

def alter(row, x, text):
    return row[:x] + text + row[x+len(text):]

def draw_head(skin, face, hair_style="short", variant=0):
    front = list(FACES[face])
    front = [r.replace(" ", "") for r in front]
    if variant:
        # Profession variants change hairline, brow geometry, mouth and beard silhouette, not just hue.
        if variant % 4 == 1:
            front[0] = "hijhijhh"; front[1] = "hj23332h"
            front[3] = "2b3333b2" if face not in ("petista", "comunista") else "2we33ew2"
            front[6] = "122rr221"
        elif variant % 4 == 2:
            front[0] = "hjjiijjh"; front[1] = "hi3333ih"
            front[2] = "2b2332b2"; front[6] = "12vrrv21"; front[7] = "11vvvv11"
        elif variant % 4 == 3:
            front[0] = "hihjjihh"; front[1] = "hh2332ih"
            front[5] = "23455332"; front[6] = "123rr321"
        if variant == 5:
            front[0] = "hhiiihhh"; front[1] = "h233332h"; front[6] = "12rrrr21"
    side_r = ["hhhiiihh", "hhiiijjh", "hiiijjii", "hiiiijhh", "hi2232hh", "h22332hh", "122222h1", "11122211"]
    side_l = ["hhiiihhh", "hjjiiihh", "iijjiiih", "hhjiiiih", "hh2322ih", "hh23322h", "1h222221", "11222111"]
    back = ["hhhhhhhh", "hhiiiihh", "hiiijjih", "hijjjjih", "hiiijjih", "hhiiiihh", "h122221h", "11222211"]
    top = ["hhhhhhhh", "hhhiiihh", "hhiiijjh", "hiiijjji", "hiijjjii", "hiiiiiih", "hhiiiihh", "hhhhhhhh"]
    bottom = ["11111111", "12222221", "12233221", "12333321", "12233221", "12222221", "11222211", "11111111"]
    if face in ("ciro", "zema"):
        side_r[2] = "hi2332hh"; side_l[2] = "hh2332ih"
        side_r[3] = "h23332hh"; side_l[3] = "hh23332h"
        top = (["hhhiiihh", "hhiiijjh", "hiiijjji", "hijjjjji", "hijjjjii", "hiiijjih", "hhiiiihh", "hhhiiihh"]
               if face == "ciro" else ["hhiijjhh", "hiiijjji", "iiiijjji", "iiiijjii", "hhiiijii", "hhiiiiih", "hhiiiihh", "hhhiiihh"])
    if face == "boulos":
        top = ["hhhiihhh", "hiiijjih", "hiihijjh", "hhijjiih", "hhiijjih", "hijiiihh", "hhiihhhh", "hhhhhhhh"]
        side_r[5:] = ["d2333vhh", "dv22vvdd", "ddvvvvdd"]
        side_l[5:] = ["hhv3332d", "ddvv22vd", "ddvvvvdd"]
        bottom = ["dddddddd", "dvvvvvvd", "dvuuuuvd", "dvu22uvd", "dv2222vd", "dv2222vd", "ddvvvvdd", "dddddddd"]
    if hair_style == "bald":
        top = ["hh2222hh", "h233332h", "23344332", "23455432", "23455432", "23344332", "h233332h", "hh2222hh"]
        side_r = ["12333321", "23344332", "22333322", "h22332hh", "hhg332hh", "dd233vhh", "dvv2vvdd", "ddvvvvdd"]
        side_l = [r[::-1] for r in side_r]
        back = ["12333321", "23344332", "22333322", "hh2222hh", "hhiiiihh", "hiiijjih", "h122221h", "11222211"]
        bottom = ["dddddddd", "dvvvvvvd", "dvuuvuvd", "dv2222vd", "dv2222vd", "dv2222vd", "ddvvvvdd", "dddddddd"]
    for side, rows in {"F": front, "R": side_r, "L": side_l, "B": back, "top": top, "bottom": bottom}.items():
        skin.face("head", side, rows)

def body_rows(style):
    # Avoid accidental checker noise: broad clusters along chest, underarms and hem.
    front = ["BCDDDDCB", "BCDDDDCB", "BCDDDDCB", "BCDDDDCB", "BCDDEDCB", "BCDDEDCB", "BCDDDDCB",
             "BCCDDCCB", "BCDDDDCB", "BCCDDCCB", "BBBBBBBB", "ABBBBBBA"]
    front[0] = "BC1331CB"
    if style in ("shirt", "rolled", "polo"):
        front[1] = "BCP33PCB"; front[2] = "BCDPPDCB"
        for y in (3, 6, 9): front[y] = alter(front[y], 3, "Q")
        if style == "polo": front[3] = "BCDQDDCB"
        if style in ("shirt", "rolled"):
            front[4] = alter(front[4], 5, "BB"); front[5] = alter(front[5], 5, "CB")
    elif style == "suit":
        front[1] = "BCPZZPCB"; front[2] = "BCPQZPCB"; front[3] = "BCBPZPCB"; front[4] = "BCBPPCBB"
        front[5] = "BCDCPCBB"; front[6] = "BCDDQCCB"; front[8] = "BCQDDCCB"
    elif style == "jacket":
        front[1] = "BCPSSPCB"; front[2] = "BCBPPCBB"
        for y in range(3, 10): front[y] = alter(front[y], 3, "Q")
        front[4] = alter(front[4], 5, "YY"); front[7] = "BCABQBAC"
    elif style == "tee":
        front[1] = "BCDPPDCB"; front[2] = "BCDDDDCB"; front[4] = "BCDPDDCB"; front[5] = "BCPPPDCB"
        front[6] = "BCDPDDCB"
    # Belt is part of clothing, never a modeled item/equipment change.
    front[11] = "SSSX YSSS".replace(" ", "")
    back = ["BCCDDCCB", "BCDDDDCB", "BCDDDDCB", "BCCDDCCB", "BCDDDDCB", "BCDDDDCB", "BCDDDDCB",
            "BCCDDCCB", "BCDDDDCB", "BCCDDCCB", "BBBBBBBB", "SSSSSSSS"]
    right = ["BCDC", "BCDC", "BCDC", "BCDC", "BCCC", "BCCC", "BCDC", "BCCD", "BCDC", "BCCC", "BBBB", "SSSS"]
    left = ["CDCB", "CDCB", "CDCB", "CDCB", "CCCB", "CCCB", "CDCB", "DCCB", "CDCB", "CCCB", "BBBB", "SSSS"]
    return {"F": front, "R": right, "L": left, "B": back, "top": ["BCCDDCCB", "BC1331CB", "BC1331CB", "BCCDDCCB"], "bottom": ["SSSSSSSS"]*4}

def draw_clothes(skin, style, variant=0):
    for side, rows in body_rows(style).items(): skin.face("body", side, rows)
    short = style in ("polo", "tee", "rolled")
    for part in ("right_arm", "left_arm"):
        for side in ("F", "R", "L", "B"):
            rows = ["BCDC", "CDDC", "CDDC", "CDDC", "BCCC", "BCDC", "CDDC", "CDDC", "BCCC", "PPPQ", "2332", "1221"]
            if short:
                rows = rows[:5] + ["PPPQ", "2332", "2343", "2332", "2332", "2332", "1221"]
            # Explicit orientation of each limb, with a watch only on the LEFT wrist.
            if part == "left_arm": rows = [row[::-1] for row in rows]
            if part == "left_arm" and side == "L" and variant % 3 == 0: rows[9] = "2SS2"
            skin.face(part, side, rows)
        skin.face(part, "top", ["BCCC", "CDDC", "CDDC", "BCCC"])
        skin.face(part, "bottom", ["1221", "2332", "2332", "1221"])
    for part in ("right_leg", "left_leg"):
        for side in ("F", "R", "L", "B"):
            rows = ["LNNM", "MNOM", "MNNM", "MNNM", "MNOM", "LNNM", "LNNM", "MNNM", "MNMM", "LMLL", "STTS", "SUUS"]
            if side == "B": rows[1:4] = ["MLLM", "MNNM", "MLLM"]
            if part == "left_leg": rows = [row[::-1] for row in rows]
            if side == "F" and variant % 2: rows[5] = "MNNM"; rows[6] = "MLLM"
            skin.face(part, side, rows)
        skin.face(part, "top", ["LNNL", "MNNM", "MNNM", "LNNL"])
        skin.face(part, "bottom", ["SSSS", "STTS", "STTS", "SSSS"])

# Distinct palette/outfit combinations for every existing profession group; no new variants or professions.
FAMILIES = {
    "comunista": [
        ("warm", "grey", "aa3439", "3c4439", "d6bb72", "polo"),
        ("brown", "black", "91343a", "414536", "c2a065", "jacket"),
        ("fair", "brown", "7b262f", "272f39", "c2b8a3", "rolled"),
        ("deep", "black", "b5393c", "31445a", "ded6bf", "shirt")],
    "bolsonarista": [
        ("bronze", "chestnut", "d9b52e", "2c4258", "397946", "polo"),
        ("fair", "brown", "387851", "344252", "d6bb4d", "rolled"),
        ("warm", "black", "cfad32", "273e51", "32704a", "tee"),
        ("brown", "salt", "46754d", "303a48", "dbc15e", "shirt"),
        ("warm", "salt", "d0af38", "293944", "356849", "polo")],
    "direitista": [
        ("fair", "brown", "30465d", "283849", "b2c0c6", "suit"),
        ("bronze", "blond", "54708a", "2d455b", "c5d0ce", "rolled"),
        ("brown", "black", "375f72", "283845", "c9c7b3", "polo"),
        ("bronze", "chestnut", "344954", "2b3541", "c6c7bc", "suit"),
        ("fair", "brown", "4f627e", "4e4b44", "c3c6c1", "shirt")],
    "petista": [
        ("warm", "chestnut", "b9433e", "2e465d", "ded8c6", "shirt"),
        ("fair", "brown", "a83836", "33495b", "e2d7c0", "polo"),
        ("bronze", "black", "c13a3b", "2f3e53", "ded8ca", "rolled"),
        ("deep", "black", "ad3c36", "444335", "d8c99f", "tee"),
        ("bronze", "black", "c0403b", "2e4861", "e1d9c8", "polo"),
        ("warm", "salt", "9d3a37", "374a5c", "d9ceba", "shirt")],
}
HERO = {
    "ciro_gomes": ("fair", "salt", "d1d7d4", "323954", "43629b", "shirt", "ciro"),
    "eneias": ("warm", "grey", "293d6a", "273653", "aeb7c1", "suit", "eneas"),
    "romeu_zema": ("fair", "zema", "a5bfca", "303a4b", "e5ddcb", "rolled", "zema"),
    "guilherme_boulos": ("warm", "black", "ac363b", "2a3d52", "c7b8a0", "polo", "boulos"),
}

def generate(item):
    group, index = item["group"], item["index"]
    if group in HERO:
        tone, hair, fabric, pants, trim, style, face = HERO[group]
        skin = Skin(palette(tone, hair, fabric, pants, trim, beard="black" if group == "eneias" else None))
        draw_head(skin, face, "bald" if group == "eneias" else "short")
    else:
        tone, hair, fabric, pants, trim, style = FAMILIES[group][index]
        skin = Skin(palette(tone, hair, fabric, pants, trim))
        # Primary silhouettes are independently composed; secondary variants preserve all mapping indices.
        from auditar import PRIMARY
        variation = 0 if index == PRIMARY[group] else index + 1
        draw_head(skin, group, variant=variation)
    draw_clothes(skin, style, index)
    if group == "ciro_gomes":
        # Blue tie, retained from the old white-shirt/blue-tie design.
        for y in range(22, 28): skin.image.putpixel((23, y), (*rgb("47669d"), 255))
        skin.image.putpixel((24, 22), (*rgb("7793ba"), 255))
    if group == "romeu_zema": skin.image.putpixel((26, 23), (*rgb("c38442"), 255))
    return skin.image

def main():
    for folder in ("novas", "previews", "comparacoes"): (OUT / folder).mkdir(parents=True, exist_ok=True)
    sources = ROOT / "assets-entrada/skins/refatoracao-8"
    sources.mkdir(parents=True, exist_ok=True)
    for item in entries():
        image = generate(item)
        path = OUT / "novas" / f"{item['key']}_skin_64x64.png"
        image.save(path)
        target = TEXTURES / item["texture"]
        shutil.copyfile(path, target)
        shutil.copyfile(path, sources / path.name)
    print("Generated and integrated 24 native 64x64 RGBA textures for eight existing egg families.")

if __name__ == "__main__": main()
