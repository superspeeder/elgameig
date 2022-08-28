package org.delusion.elgame.tile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import org.delusion.elgame.inventory.Stack;
import org.delusion.elgame.item.Item;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.utils.Vector2i;
import org.delusion.elgame.world.World;
import org.delusion.elgame.world.World.Layer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TileType {
    private final int id;
    private final String visibleName;
    private final TileProperties properties;
    private final String textureName;
    private static final Map<Integer, TileType> tilesById = new HashMap<>();
    private static TextureAtlas mainAtlas;

    public TileType(int id, String visibleName, TileProperties properties, String textureName) {
        this.id = id;
        this.visibleName = visibleName;
        this.properties = properties;
        this.textureName = textureName;
        tilesById.put(id, this);
    }

    public static void load() {
        mainAtlas = new TextureAtlas(Gdx.files.internal("textures/tileAtlas.atlas"));
    }

    public static boolean isSolid(TileType tile) {
        return tile != null && tile.getProperties().solid;
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
        return tilesById.getOrDefault(id, TileTypes.Air);
    }

    public void renderTo(SpriteBatch batch, int x, int y, World world, TileMetadata metadata) {
        if (properties.visible) {
            float lv = Math.min(1.f,metadata.lightValue);
            batch.setColor(lv, lv, lv, 1.0f);
            batch.draw(mainAtlas.findRegion(textureName, id), x * World.TILE_SIZE, y * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
        }
    }

    public Stream<Stack> getDrop(Player player, World world, Vector2i tilePos, Stack stack, Layer layer) {
        return properties.dropFunc.getDrops(player, world, tilePos, stack, layer);
    }

    public void renderToBG(SpriteBatch batch, int x, int y, World world, TileMetadata metadata) {
        if (properties.visible) {
            float lv = Math.min(1.f,metadata.lightValue) * 0.6f;
            batch.setColor(lv, lv, lv, 1.0f);
            batch.draw(mainAtlas.findRegion(textureName, id), x * World.TILE_SIZE, y * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);
        }
    }
}
