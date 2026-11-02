"""Present actual atlas renders, never a substitute illustration, and keep UVs visibly separate."""
import sys
from pathlib import Path
from PIL import Image, ImageDraw

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
sys.path.insert(0, str(HERE))
sys.path.insert(0, str(ROOT / "arte/refatoracao-skins"))
from auditar import OUT, entries, montage, font, PRIMARY, HEROES, TITLES
from render_skin import render

BG, PANEL, INK, DIM = "#111827", "#202b3c", "#edf2fa", "#afbdcf"

def canvas(size): return Image.new("RGB", size, BG)

def pixel(image, factor): return image.resize((image.width*factor, image.height*factor), Image.Resampling.NEAREST)

def atlas(path, factor=6):
    skin = Image.open(path).convert("RGBA")
    board = Image.new("RGB", (64*factor, 64*factor), "#414c5b")
    draw = ImageDraw.Draw(board)
    for y in range(0, 64, 4):
        for x in range(0, 64, 4):
            if (x//4+y//4) % 2: draw.rectangle((x*factor, y*factor, (x+4)*factor-1, (y+4)*factor-1), fill="#344050")
    board.paste(pixel(skin, factor), (0, 0), pixel(skin, factor))
    return board

def presentation(item):
    path = OUT / "novas" / f"{item['key']}_skin_64x64.png"
    sheet = canvas((1540, 720)); draw = ImageDraw.Draw(sheet)
    draw.text((28, 18), f"{item['title'].upper()}  |  GUERRA EZUBERANTE", fill=INK, font=font(30))
    draw.text((28, 62), "64 × 64 RGBA · STEVE CLÁSSICO · 4 PX POR BRAÇO · VISTAS CALCULADAS A PARTIR DO ATLAS", fill=DIM, font=font(17))
    for index, (yaw, title) in enumerate(((0,"FRENTE"),(180,"COSTAS"),(-90,"LATERAL ESQUERDA"),(90,"LATERAL DIREITA"))):
        x = 20 + index*235
        draw.rounded_rectangle((x, 110, x+221, 605), radius=10, fill=PANEL)
        figure = render(path, yaw, 0, 10)
        sheet.paste(figure, (x-9, 152), figure)
        draw.text((x+12, 567), title, font=font(15), fill=INK)
    board = atlas(path)
    sheet.paste(board, (982, 114))
    draw.text((982, 511), "ATLAS UV · arquivo de jogo separado", font=font(17), fill=INK)
    face = pixel(Image.open(path).crop((8,8,16,16)), 17)
    sheet.paste(face, (982, 547), face)
    draw.text((1144, 565), "ROSTO 8 × 8", font=font(20), fill=INK)
    draw.text((1144, 598), item["model"], font=font(15), fill=DIM)
    sheet.save(OUT / "previews" / f"{item['key']}_preview.png")

def comparison(item):
    before = OUT / "originais" / f"{item['key']}.png"
    after = OUT / "novas" / f"{item['key']}_skin_64x64.png"
    sheet = canvas((1400, 650)); draw = ImageDraw.Draw(sheet)
    draw.text((24, 18), f"{item['title'].upper()} · ANTES / DEPOIS", font=font(29), fill=INK)
    for index, (path, title) in enumerate(((before,"ANTES"),(after,"DEPOIS"))):
        x = 20 + index*690
        draw.rounded_rectangle((x,80,x+666,630), 10, fill=PANEL)
        draw.text((x+20,96), title, font=font(23), fill=INK)
        for at, yaw, label in ((12,0,"FRENTE"),(205,180,"COSTAS")):
            image = render(path,yaw,0,8)
            sheet.paste(image,(x+at,160),image)
            draw.text((x+at+36,468),label,font=font(16),fill=DIM)
        face = pixel(Image.open(path).convert("RGBA").crop((8,8,16,16)), 25)
        sheet.paste(face,(x+437,200),face)
        draw.text((x+437,420),"ROSTO",font=font(17),fill=DIM)
        image = render(path,32,13,6)
        sheet.paste(image,(x+224,402),image)
    sheet.save(OUT / "comparacoes" / f"{item['key']}_antes-depois.png")

def main(keys=None):
    data = list(entries())
    montage([(e["title"], OUT/"novas"/f"{e['key']}_skin_64x64.png") for e in data], OUT/"depois-24-texturas.png", "DEPOIS · 24 texturas dos oito ovos")
    primary = [e for e in data if e["group"] in HEROES or e["index"] == PRIMARY.get(e["group"])]
    montage([(TITLES[e["group"]], OUT/"novas"/f"{e['key']}_skin_64x64.png") for e in primary], OUT/"oito-personagens.png", "OITO PERSONAGENS · aparência principal", columns=4)
    heads = canvas((1320, 680)); draw = ImageDraw.Draw(heads)
    draw.text((20,16), "TOPO E BASE DA CABEÇA · UV REAL", font=font(26), fill=INK)
    for i,item in enumerate(primary):
        x,y=(i%4)*330,(i//4)*300+62
        draw.text((x+12,y),TITLES[item["group"]],font=font(20),fill=INK)
        path=OUT/"novas"/f"{item['key']}_skin_64x64.png"
        for offset,pitch,label in ((5,60,"TOPO"),(170,-60,"BASE / MANDÍBULA")):
            image=render(path,32,pitch,10,only={"head","head_overlay"})
            heads.paste(image,(x+offset,y+48),image)
            draw.text((x+offset,y+192),label,font=font(14),fill=DIM)
    heads.save(OUT/"topos-e-bases.png")
    selected = [item for item in data if not keys or item["key"] in keys]
    for item in selected:
        presentation(item); comparison(item)
    print(f"{len(selected)} atlas previews and before/after comparisons; eight-character and 24-variant sheets rendered.")

if __name__ == "__main__": main(sys.argv[1:] or None)
