package org.delusion.elgame.tile;

public class TileProperties {
    private boolean solid = true;
    private boolean visible = true;

    public TileProperties() {
    }

    public TileProperties solid() {
        solid = true;
        return this;
    }

    public TileProperties intangible() {
        solid = false;
        return this;
    }

    public TileProperties visible() {
        visible = true;
        return this;
    }
    public TileProperties invisible() {
        visible = false;
        return this;
    }

}
