package org.delusion.elgame.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.delusion.elgame.item.tool.ToolType;
import org.delusion.elgame.tile.TileType;

import java.util.*;

public class Item {
    private static Map<Item, TextureRegion> textures = new HashMap<>();
    private static Set<Item> items = new HashSet<>();

    private static TextureAtlas atlas;
    private static boolean loaded = false;


    public static void load() {
        atlas = new TextureAtlas(Gdx.files.internal("textures/item/items.atlas"));
        for (Item item : items) {
            textures.put(item, atlas.findRegion(item.name));
        }

        loaded = true;
    }

    public final String name;
    private Properties properties;

    public Item(String name, Properties properties) {
        this.name = name;
        this.properties = properties;

        items.add(this);
        if (loaded) {
            textures.put(this, atlas.findRegion(name));
        }
    }

    public TextureRegion getTex() {
        return textures.get(this);
    }

    public int maxStackSize() {
        return properties.maxStackSize;
    }

    public ItemUsageAction primary() {
        return properties.primary;
    }

    public ItemUsageAction primaryDragged() {
        return properties.primaryDragged;
    }

    public ItemUsageAction secondary() {
        return properties.secondary;
    }

    public ItemUsageAction secondaryDragged() {
        return properties.secondaryDragged;
    }

    public Set<ToolType> getToolTypes() {
        return Collections.unmodifiableSet(properties.toolTypes);
    }

    public static class Properties {
        public Set<ToolType> toolTypes;
        private int maxStackSize;
        private ItemUsageAction primary, secondary;
        private ItemUsageAction primaryDragged, secondaryDragged;

        public Properties maxStackSize(int maxStackSize) {
            this.maxStackSize = maxStackSize;
            return this;
        }

        public Properties primary(ItemUsageAction primary) {
            this.primary = primary;
            return this;
        }

        public Properties primaryDragged(ItemUsageAction primaryDragged) {
            this.primaryDragged = primaryDragged;
            return this;
        }

        public Properties secondary(ItemUsageAction act) {
            secondary = act;
            return this;
        }

        public Properties secondaryDragged(ItemUsageAction secondaryDragged) {
            this.secondaryDragged = secondaryDragged;
            return this;
        }

        public Properties toolTypes(ToolType... types) {
            toolTypes = Set.of(types);
            return this;
        }
    }

    public static class Builder {
        private String name;
        private Properties properties;

        private Builder(String name, Properties properties) {
            this.name = name;
            this.properties = properties;
        }

        public static Builder create(String name, int maxStackSize) {
            return new Builder(name, new Properties().maxStackSize(maxStackSize));
        }

        public Item build() {
            return new Item(name, properties);
        }

        public Builder placeResult(TileType ttype) {
            System.out.println(ttype.getVisibleName());
            properties.primaryDragged(ItemUsageAction.placeTile(ttype)).primary(ItemUsageAction.placeTile(ttype));
            return this;
        }

        public Builder secondary(ItemUsageAction act) {
            properties.secondary(act);
            return this;
        }

        public Builder secondaryDragged(ItemUsageAction act) {
            properties.secondaryDragged(act);
            return this;
        }

        public Builder primary(ItemUsageAction act) {
            properties.primary(act);
            return this;
        }

        public Builder primaryDragged(ItemUsageAction act) {
            properties.primaryDragged(act);
            return this;
        }

        public Builder toolTypes(ToolType... types) {
            properties.toolTypes(types);
            return this;
        }
    }
}
