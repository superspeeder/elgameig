package org.delusion.elgame.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.gson.*;
import org.delusion.elgame.item.tool.ToolType;
import org.delusion.elgame.tile.TileType;

import java.lang.reflect.Type;
import java.util.*;

public class Item {
    private static Map<Item, TextureRegion> textures = new HashMap<>();
    private static Map<String, Item> itemMap = new HashMap<>();
    private static Set<Item> items = new HashSet<>();

    private static TextureAtlas atlas;
    private static boolean loaded = false;

    public static Item getByName(String name) {
        return itemMap.getOrDefault(name, null);
    }


    public static void load() {
        atlas = new TextureAtlas(Gdx.files.internal("textures/item/items.atlas"));
        for (Item item : items) {
            textures.put(item, atlas.findRegion(item.name));
        }

        loaded = true;
    }

    public final String name;
    private transient Properties properties;

    public Item(String name, Properties properties) {
        this.name = name;
        this.properties = properties;

        items.add(this);
        if (loaded) {
            textures.put(this, atlas.findRegion(name));
        }

        itemMap.put(name, this);
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

    public float toolStrength() {
        return properties.toolStrength;
    }

    public float toolSpeed() {
        return properties.toolSpeed;
    }

    public boolean canBreak(TileType tileType) {
        for (ToolType tt : tileType.getProperties().breakingTools) {
            if (getToolTypes().contains(tt)) return true;
        }
        return false;
    }

    public static class Properties {
        private Set<ToolType> toolTypes;
        private int maxStackSize;
        private ItemUsageAction primary = ItemUsageAction.none, secondary = ItemUsageAction.none;
        private ItemUsageAction primaryDragged = ItemUsageAction.none, secondaryDragged = ItemUsageAction.none;
        private float toolStrength, toolSpeed;

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

        public Properties toolStrength(float strength) {
            toolStrength = strength;
            return this;
        }

        public void toolSpeed(float toolSpeed) {
            this.toolSpeed = toolSpeed;
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
            primaryDragged(ItemUsageAction.placeTile(ttype)).primary(ItemUsageAction.placeTile(ttype));
            secondaryDragged(ItemUsageAction.placeTileBg(ttype)).secondary(ItemUsageAction.placeTileBg(ttype));
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

		public Builder miningTool(float strength, float speed) {
            toolStrength(strength);
            toolSpeed(speed);
            primary(ItemUsageAction.mineTile).primaryDragged(ItemUsageAction.mineTile);
            secondary(ItemUsageAction.mineTileBg).secondaryDragged(ItemUsageAction.mineTileBg);
			return this;
		}

        public Builder toolStrength(float strength) {
            properties.toolStrength(strength);
            return this;
        }

        public Builder toolSpeed(float speed) {
            properties.toolSpeed(speed);
            return this;
        }

        public Builder placeResultForeground(TileType ttype) {
            primaryDragged(ItemUsageAction.placeTile(ttype)).primary(ItemUsageAction.placeTile(ttype));
            return this;
        }

        public Builder placeResultBackground(TileType ttype) {
            secondaryDragged(ItemUsageAction.placeTile(ttype)).secondary(ItemUsageAction.placeTile(ttype));
            return this;
        }
    }

    public static class TypeAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
        @Override
        public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.name);
        }

        @Override
        public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Item.getByName(json.getAsString());
        }
    }
}
