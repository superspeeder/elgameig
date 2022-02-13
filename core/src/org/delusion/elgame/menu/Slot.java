package org.delusion.elgame.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Slot {

    private static TextureAtlas slotAtlas;

    public static void load() {
        slotAtlas = new TextureAtlas(Gdx.files.internal("textures/ui/slotAtlas.atlas"));
    }

    public static TextureAtlas getSlotAtlas() {
        return slotAtlas;
    }
}
