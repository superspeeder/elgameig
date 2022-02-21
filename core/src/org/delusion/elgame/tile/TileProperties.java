package org.delusion.elgame.tile;

import org.delusion.elgame.inventory.Stack;
import org.delusion.elgame.item.tool.ToolType;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TileProperties {
    public final boolean solid;
    public final boolean visible;
    public final TileDropsFunction dropFunc;
    public final Set<ToolType> breakingTools;

    public TileProperties(boolean solid, boolean visible, TileDropsFunction dropFunc, Set<ToolType> breakingTools) {
        this.solid = solid;
        this.visible = visible;
        this.dropFunc = dropFunc;
        this.breakingTools = Collections.unmodifiableSet(breakingTools);
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {

        private boolean solid = true;
        private boolean visible = true;
        private TileDropsFunction dropFunc = TileDropsFunction.none;
        private Set<ToolType> breakingTools = Set.of();

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
            return new TileProperties(solid, visible, dropFunc, breakingTools);
        }

        public Builder drops(TileDropsFunction f) {
            dropFunc = f;
            return this;
        }

        public Builder breakingTools(ToolType... types) {
            breakingTools = Set.of(types);
            return this;
        }
    }
}
