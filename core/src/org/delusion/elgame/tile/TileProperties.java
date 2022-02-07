package org.delusion.elgame.tile;

public class TileProperties {
    public final boolean solid;
    public final boolean visible;

    public TileProperties(boolean solid, boolean visible) {
        this.solid = solid;
        this.visible = visible;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {

        private boolean solid = true;
        private boolean visible = true;

        private Builder() {
        }

        public Builder solid() {
            solid = true;
            return this;
        }

        public Builder intangible() {
            solid = false;
            return this;
        }

        public Builder visible() {
            visible = true;
            return this;
        }

        public Builder invisible() {
            visible = false;
            return this;
        }

        public TileProperties build() {
            return new TileProperties(solid, visible);
        }
    }
}
