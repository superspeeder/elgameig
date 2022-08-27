import json
import os
import sys

def runTexturePacker(classpath, inpath, outpath, name):
    sys.stdout.write(f"Packing textures at {os.path.abspath(os.path.join('textures', inpath))} into {os.path.abspath(os.path.join('assets/textures/', outpath, name + '.atlas'))}\r\n")
    sys.stdout.flush()
    if os.system(f"java -cp {classpath} com.badlogic.gdx.tools.texturepacker.TexturePacker {os.path.join('textures', inpath)} {os.path.join('assets/textures/', outpath)} {name}"):
        raise RuntimeError("Failed to pack textures")

if __name__ == "__main__":
    print("Packing Textures")

    with open("texturePacking.json") as f:
        tpacking_data = json.load(f)

    for path in tpacking_data:
        runTexturePacker(sys.argv[1], *path)