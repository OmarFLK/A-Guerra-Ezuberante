"""Preserve the 24 existing texture files behind the eight egg families; render the baseline."""
import json, sys, shutil, hashlib
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / "arte/refatoracao-skins"))
from render_skin import render

OUT = ROOT / "docs/arte/refatoracao-skins-8-personagens"
TEXTURES = ROOT / "src/main/resources/assets/guerra_ezuberante/textures/entity"
GROUPS = {"comunista": 4, "bolsonarista": 5, "direitista": 5, "petista": 6}
HEROES = ["ciro_gomes", "eneias", "romeu_zema", "guilherme_boulos"]
TITLES = {"comunista": "Comunista", "bolsonarista": "Bolsonarista", "direitista": "Direitista", "petista": "Petista",
          "ciro_gomes": "Ciro Gomes", "eneias": "Enéas Carneiro", "romeu_zema": "Romeu Zema", "guilherme_boulos": "Guilherme Boulos"}
PRIMARY = {"comunista": 1, "bolsonarista": 4, "direitista": 3, "petista": 4}

def entries():
    for group, count in GROUPS.items():
        for i in range(count):
            yield {"key": f"{group}_{i}", "group": group, "index": i, "title": f"{TITLES[group]} · {i}",
                   "texture": f"{group}/{group}_{i}.png", "model": "PlayerModel Steve 4 px"}
    for hero in HEROES:
        yield {"key": hero, "group": hero, "index": 0, "title": TITLES[hero], "texture": f"{hero}.png", "model": "GeckoLib humanoid.geo.json"}

def font(size):
    return ImageFont.truetype("C:/Windows/Fonts/segoeui.ttf", size)

def montage(paths, destination, title, columns=6):
    rows = (len(paths) + columns - 1) // columns
    sheet = Image.new("RGB", (columns * 220, rows * 270 + 70), "#101725")
    draw = ImageDraw.Draw(sheet)
    draw.text((20, 16), title, font=font(25), fill="#ecf1fb")
    for index, (label, path) in enumerate(paths):
        x, y = (index % columns) * 220, (index // columns) * 270 + 60
        figure = render(path, 0, 0, 6)
        sheet.paste(figure, (x + 38, y), figure)
        draw.text((x + 12, y + 225), label, font=font(16), fill="#d2dfed")
    sheet.save(destination)

def main():
    (OUT / "originais").mkdir(parents=True, exist_ok=True)
    manifest = []
    for item in entries():
        original = OUT / "originais" / f"{item['key']}.png"
        if not original.exists():
            shutil.copyfile(TEXTURES / item["texture"], original)
        item["original_sha256"] = hashlib.sha256(original.read_bytes()).hexdigest()
        manifest.append(item)
    (OUT / "manifesto.json").write_text(json.dumps(manifest, ensure_ascii=False, indent=2), encoding="utf-8")
    montage([(e["title"], OUT / "originais" / f"{e['key']}.png") for e in manifest], OUT / "antes-24-texturas.png", "ANTES · texturas reais dos oito ovos")
    refs = [("Lula", "lula_skin_64x64.png"), ("Jair", "jair_bolsonaro.png"), ("Flávio", "flavio_bolsonaro.png"),
            ("Renan", "renan_santos.png"), ("Arthur", "arthur_do_val.png"), ("Ucraniana", "combatente_ucraniana.png")]
    montage([(name, TEXTURES / path) for name, path in refs], OUT / "referencias-aprovadas.png", "REFERÊNCIAS APROVADAS · sem alteração")
    print(f"Preserved {len(manifest)} original textures and rendered baseline/reference sheets.")

if __name__ == "__main__":
    main()
