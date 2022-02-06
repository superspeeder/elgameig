package org.delusion.elgame.tile;

import java.util.HashMap;
import java.util.Map;

public enum TileType {
    Air(0, "Air", new TileProperties().intangible().invisible(), null),
    Dirt(1, "Dirt", new TileProperties(), "tile"),
    ;


    private final int id;
    private final String visibleName;
    private final TileProperties properties;
    private final String textureName;
    private static Map<Integer, TileType> tilesById = new HashMap<>();

    TileType(int id, String visibleName, TileProperties properties, String textureName) {
        this.id = id;
        this.visibleName = visibleName;
        this.properties = properties;
        this.textureName = textureName;
    }

    static {
        for (TileType value : values()) {
            tilesById.put(value.id, value);
        }
    }

    public int getId() {
        return id;
    }

    public String getVisibleName() {
        return visibleName;
    }

    public TileProperties getProperties() {
        return properties;
    }

    public String getTextureName() {
        return textureName;
    }

    public static TileType fromId(int id) {
        return tilesById.getOrDefault(id, TileType.Air);
    }
}
