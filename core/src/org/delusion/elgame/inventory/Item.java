package org.delusion.elgame.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public enum Item {
    Dirt("dirt", 200),
    Grass("grass", 200),
    Stone("stone", 200),
    ;

    private final String name;
    public final int maxStackSize;

    private static Map<Item, TextureRegion> textures = new HashMap<>();
    private static TextureAtlas atlas;

    Item(String name, int maxStackSize) {
        this.name = name;
        this.maxStackSize = maxStackSize;
    }

    public static void load() {
        atlas = new TextureAtlas(Gdx.files.internal("textures/item/items.atlas"));

        for (Item i : values()) {
            textures.put(i, atlas.findRegion(i.name));
        }
    }

    public TextureRegion getTex() {
        return textures.get(this);
    }
}
