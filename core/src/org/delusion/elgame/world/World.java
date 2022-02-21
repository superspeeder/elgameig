package org.delusion.elgame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.tile.TileTypes;
import org.delusion.elgame.utils.AABBi;
import org.delusion.elgame.utils.SimpleRenderable;
import org.delusion.elgame.utils.Vector2i;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class World implements SimpleRenderable {

    public enum Layer {
        Main, BG
    }

    public static final int TILE_SIZE = 16;
    private static final long MAX_CHUNKS = 100;

    private final ElGame game;
    private final AsyncLoadingCache<Vector2i, Chunk> chunkCache = Caffeine.newBuilder()
            .maximumSize(MAX_CHUNKS)
            .evictionListener(Chunk::evictionListener)
            .buildAsync(pos -> new Chunk(this, pos));
    private final SpriteBatch batch;
    private final SpriteBatch entityBatch;
    private Player player;

    public World(ElGame game) {
        this.game = game;
        batch = new SpriteBatch();
        entityBatch = new SpriteBatch();
    }

    public static Vector2i toTilePos(Vector2 translated) {
        return new Vector2i(Math.floorDiv((int) translated.x, TILE_SIZE), Math.floorDiv((int) translated.y, TILE_SIZE));
    }

    public CompletableFuture<TileType> getTile(int x, int y) {
        Vector2i chunkPos = new Vector2i(Math.floorDiv(x, Chunk.SIZE), Math.floorDiv(y, Chunk.SIZE));
        Vector2i localPos = new Vector2i(Math.floorMod(x, Chunk.SIZE), Math.floorMod(y, Chunk.SIZE));

        if (localPos.x < 0) {
            localPos.x = Chunk.SIZE - localPos.x;
        }

        if (localPos.y < 0) {
            localPos.y = Chunk.SIZE - localPos.y;
        }

        return chunkCache.get(chunkPos).thenApply(chunk -> chunk.get(localPos.x, localPos.y));
    }

    public void setTile(int x, int y, TileType ttype) {
        Vector2i chunkPos = new Vector2i(Math.floorDiv(x, Chunk.SIZE), Math.floorDiv(y, Chunk.SIZE));
        Vector2i localPos = new Vector2i(Math.floorMod(x, Chunk.SIZE), Math.floorMod(y, Chunk.SIZE));

        if (localPos.x < 0) {
            localPos.x = Chunk.SIZE - localPos.x;
        }

        if (localPos.y < 0) {
            localPos.y = Chunk.SIZE - localPos.y;
        }

        chunkCache.get(chunkPos).thenAccept(chunk -> chunk.set(localPos.x, localPos.y, ttype));
    }

    @Override
    public void render() {
        batch.setTransformMatrix(game.getPlayer().getCamera().view);
        batch.setProjectionMatrix(game.getPlayer().getCamera().projection);
        Vector2 playerPos = game.getPlayer().getPosition();

        Vector2i tlc = player.tileFromScreenPos(0, 0);
        Vector2i brc = player.tileFromScreenPos(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        AABBi viewableArea = new AABBi(
                tlc.x, brc.x, brc.y, tlc.y
        );

        int leftC = Math.floorDiv(viewableArea.left, Chunk.SIZE);
        int rightC = (int) Math.ceil((double)viewableArea.right / (double)Chunk.SIZE);

        if (viewableArea.left / (float)Chunk.SIZE < leftC) leftC -= 1;
        if (viewableArea.right / (float)Chunk.SIZE > rightC) rightC += 1;

        int bottomC = Math.floorDiv(viewableArea.bottom, Chunk.SIZE);
        int topC = (int) Math.ceil((double)viewableArea.top / (double)Chunk.SIZE);

        if (viewableArea.bottom / (float)Chunk.SIZE < bottomC) bottomC -= 1;
        if (viewableArea.top / (float)Chunk.SIZE > topC) topC += 1;

        batch.begin();

        for (int cx = leftC ; cx <= rightC ; cx++) {
            for (int cy = bottomC ; cy <= topC ; cy++) {
                renderChunk(cx,cy);
            }
        }

        Vector2i tp = player.tileFromScreenPos(Gdx.input.getX(), Gdx.input.getY());
        batch.draw(player.getSelectionBox(), tp.x * World.TILE_SIZE, tp.y * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);

        batch.end();


        renderEntities();
    }

    void renderEntities() {
        entityBatch.setProjectionMatrix(player.getCamera().projection);
        entityBatch.setTransformMatrix(game.getPlayer().getCamera().view);
        entityBatch.begin();
        game.getPlayer().render();
        entityBatch.end();
    }

    private void renderChunk(int cx, int cy) {
        Chunk nc = chunkCache.get(new Vector2i(cx, cy)).getNow(null);
        if (nc != null) {
            nc.renderTo(batch);
        }
        batch.setColor(Color.WHITE);
    }

    public void linkToPlayer(Player player) {
        this.player = player;
        batch.setProjectionMatrix(player.getCamera().projection);
        entityBatch.setProjectionMatrix(player.getCamera().projection);
    }

    public Batch getEntityBatch() {
        return entityBatch;
    }

    public ElGame getGame() {
        return game;
    }

    public CompletableFuture<TileType> getTile(Vector2i tilePos) {
        return getTile(tilePos.x, tilePos.y);
    }

    public void setTile(Vector2i tilePos, TileType ttype) {
        setTile(tilePos.x, tilePos.y, Objects.requireNonNullElse(ttype, TileTypes.Air));
    }

    public boolean canPlaceAt(Vector2i tilePos) {
        return getTile(tilePos).getNow(null) == TileTypes.Air && !collidesWithEntities(tilePos);
    }

    private boolean collidesWithEntities(Vector2i tilePos) {
        if (player.collidesWithTile(tilePos)) {
            return true;
        }
        return false;
    }

    public boolean canPlaceAtBg(Vector2i tilePos) {
        return getTileBg(tilePos).getNow(null) == TileTypes.Air;
    }

    public CompletableFuture<TileType> getTileBg(Vector2i tilePos) {
        return getTileBg(tilePos.x, tilePos.y);
    }

    public CompletableFuture<TileType> getTileBg(int x, int y) {
        Vector2i chunkPos = new Vector2i(Math.floorDiv(x, Chunk.SIZE), Math.floorDiv(y, Chunk.SIZE));
        Vector2i localPos = new Vector2i(Math.floorMod(x, Chunk.SIZE), Math.floorMod(y, Chunk.SIZE));

        if (localPos.x < 0) {
            localPos.x = Chunk.SIZE - localPos.x;
        }

        if (localPos.y < 0) {
            localPos.y = Chunk.SIZE - localPos.y;
        }

        return chunkCache.get(chunkPos).thenApply(chunk -> chunk.getBg(localPos.x, localPos.y));
    }

    public void setTileBg(Vector2i tilePos, TileType ttype) {
        setTileBg(tilePos.x, tilePos.y, Objects.requireNonNullElse(ttype, TileTypes.Air));
    }

    public void setTileBg(int x, int y, TileType ttype) {
        Vector2i chunkPos = new Vector2i(Math.floorDiv(x, Chunk.SIZE), Math.floorDiv(y, Chunk.SIZE));
        Vector2i localPos = new Vector2i(Math.floorMod(x, Chunk.SIZE), Math.floorMod(y, Chunk.SIZE));

        if (localPos.x < 0) {
            localPos.x = Chunk.SIZE - localPos.x;
        }

        if (localPos.y < 0) {
            localPos.y = Chunk.SIZE - localPos.y;
        }

        chunkCache.get(chunkPos).thenAccept(chunk -> chunk.setBg(localPos.x, localPos.y, ttype));
    }
}
