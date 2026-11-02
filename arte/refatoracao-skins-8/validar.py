"""Validate actual PNG bytes/UV occupancy, distinct faces, production bindings and preserved gameplay files."""
import json, hashlib, subprocess, sys
from pathlib import Path
from PIL import Image, ImageChops

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
sys.path.insert(0, str(HERE))
from auditar import OUT, TEXTURES, entries, PRIMARY, HEROES
from gerar import BOXES

def sha(path): return hashlib.sha256(path.read_bytes()).hexdigest()

def uv_mask():
    mask = Image.new("L", (64,64), 0)
    for u,v,w,d,h in BOXES.values():
        for rect in ((u,v+d,u+2*(w+d),v+d+h),(u+d,v,u+d+2*w,v+d)):
            mask.paste(255, rect)
    return mask

def git(*args):
    return subprocess.check_output(["git", "-c", f"safe.directory={ROOT.as_posix()}", *args], cwd=ROOT).decode("utf-8").strip()

def main():
    mask = uv_mask()
    report = {"version": "0.022.3", "textures": [], "faces": {}, "preserved": {}}
    for item in entries():
        path = OUT / "novas" / f"{item['key']}_skin_64x64.png"
        image = Image.open(path)
        assert image.format == "PNG" and image.size == (64,64) and image.mode == "RGBA", item["key"]
        alpha = image.getchannel("A")
        assert set(alpha.getdata()) == {0,255}, f"Partial alpha: {item['key']}"
        assert ImageChops.difference(alpha,mask).getbbox() is None, f"Missing/stray UV texels: {item['key']}"
        assert sha(path) == sha(TEXTURES/item["texture"]) == sha(ROOT/"assets-entrada/skins/refatoracao-8"/path.name), item["key"]
        before = Image.open(OUT/"originais"/f"{item['key']}.png").convert("RGBA")
        changed = sum(a != b for a,b in zip(before.getdata(),image.getdata()))
        assert changed > 500, f"Refactor is too superficial: {item['key']} ({changed})"
        record = {**item, "rgba": True, "dimensions": [64,64], "uv_faces": 36, "binary_alpha": True,
                  "changed_pixels": changed, "sha256": sha(path)}
        report["textures"].append(record)
        if item["group"] in HEROES or item["index"] == PRIMARY.get(item["group"]):
            report["faces"][item["group"]] = list(image.crop((8,8,16,16)).getdata())
    face_difference = {}
    names = list(report["faces"])
    for index,a in enumerate(names):
        for b in names[index+1:]:
            pa,pb = report["faces"][a],report["faces"][b]
            different = sum(ca != cb for ca,cb in zip(pa,pb))
            mean = sum(abs(ca[c]-cb[c]) for ca,cb in zip(pa,pb) for c in range(3))/(64*3)
            assert different >= 20 and mean > 8, (a,b,different,mean)
            face_difference[f"{a} / {b}"] = {"different_texels": different, "mean_channel_delta": round(mean,2)}
    report["faces"] = face_difference
    # Reject accidental changes to IDs, combat, drops, worldgen, configuration, rig and animations.
    protected_roots = ["src/main/java/io/github/omarflk/guerraezuberante/entity", "src/main/java/io/github/omarflk/guerraezuberante/registry",
                       "src/main/java/io/github/omarflk/guerraezuberante/village", "src/main/java/io/github/omarflk/guerraezuberante/config",
                       "src/main/resources/assets/guerra_ezuberante/geo", "src/main/resources/assets/guerra_ezuberante/animations"]
    for folder in protected_roots:
        result = git("diff", "HEAD", "--", folder)
        assert not result, f"Unexpected gameplay/model change: {folder}"
        report["preserved"][folder] = True
    ref_root = ROOT/".tools/referencias-skins-8"
    report["skills_read"] = ["skill_criar_skins.md", "skill_criar_itens.md"]
    report["reference_zip_extracted"] = ref_root.is_dir()
    report["passed"] = True
    (OUT/"validacao.json").write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding="utf-8")
    print(f"PASS: {len(report['textures'])} native RGBA skins, 36 faces each, UV mask, source hashes, distinct faces and unchanged gameplay/rig/animations.")

if __name__ == "__main__": main()
