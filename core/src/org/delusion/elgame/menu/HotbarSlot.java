package org.delusion.elgame.menu;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.delusion.elgame.inventory.Stack;

public class HotbarSlot extends Slot {
    private final String texname;
    private final String selectedTexName;
    private final Hotbar hotbar;
    private final TextureRegion normalTex, selectedTex;

    public HotbarSlot(String texname, String selectedTexName, int id, Hotbar hotbar, Stack currentStack) {
        super(texname, id, currentStack);
        this.texname = texname;
        this.selectedTexName = selectedTexName;
        this.hotbar = hotbar;
        normalTex = this.tex;
        selectedTex = slotAtlas.findRegion(selectedTexName);
    }

    public void deselected() {
        tex = normalTex;
    }

    public void selected() {
        tex = selectedTex;
    }
}
