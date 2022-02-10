package org.delusion.elgame.tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.delusion.elgame.world.World;

import java.util.HashMap;
import java.util.Map;

public enum TileType {
    Air(0, "Air", TileProperties.builder().intangible().invisible().build(), null),
    Dirt(1, "Dirt", TileProperties.builder().build(), "tile"),
    StoneTile(2, "Stone Tile", TileProperties.builder().build(), "tile"),
    ;


    private final int id;
    private final String visibleName;
    private final TileProperties properties;
    private final String textureName;
    private static Map<Integer, TileType> tilesById = new HashMap<>();
    private static TextureAtlas mainAtlas;

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

    public static void load() {
        mainAtlas = new TextureAtlas(Gdx.files.internal("textures/tileAtlas.atlas"));
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

    public void renderTo(SpriteBatch batch, int x, int y) {
        if (properties.visible) {
            // render tile
            batch.draw(mainAtlas.findRegion(textureName, id), x * World.TILE_SIZE, y * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
        }
    }
}
