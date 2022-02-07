package org.delusion.elgame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.utils.AABBi;
import org.delusion.elgame.utils.SimpleRenderable;
import org.delusion.elgame.utils.Vector2i;

import java.util.concurrent.CompletableFuture;

public class World implements SimpleRenderable {

    public static final int TILE_SIZE = 8;
    private static final long MAX_CHUNKS = 100;

    private final ElGame game;
    private final AsyncLoadingCache<Vector2i, Chunk> chunkCache = Caffeine.newBuilder()
            .maximumSize(MAX_CHUNKS)
            .evictionListener(Chunk::evictionListener)
            .buildAsync(pos -> new Chunk(this, pos));
    private final SpriteBatch batch;
    private final SpriteBatch entityBatch;

    public World(ElGame game) {
        this.game = game;
        batch = new SpriteBatch();
        entityBatch = new SpriteBatch();

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
        Vector2 playerPos = game.getPlayer().getPosition();
        AABBi viewableArea = new AABBi(
                (int)(playerPos.x - Gdx.graphics.getWidth() / 2.0) / TILE_SIZE,
                (int)(playerPos.x + Gdx.graphics.getWidth() / 2.0) / TILE_SIZE,
                (int)(playerPos.y - Gdx.graphics.getHeight() / 2.0) / TILE_SIZE,
                (int)(playerPos.y + Gdx.graphics.getHeight() / 2.0) / TILE_SIZE
        );

        int leftC = Math.floorDiv(viewableArea.left, Chunk.SIZE);
        int rightC = (int) Math.ceil((double)viewableArea.right / (double)Chunk.SIZE);

        int bottomC = Math.floorDiv(viewableArea.bottom, Chunk.SIZE);
        int topC = (int) Math.ceil((double)viewableArea.top / (double)Chunk.SIZE);

        batch.begin();

        for (int cx = leftC ; cx <= rightC ; cx++) {
            for (int cy = bottomC ; cy <= topC ; cy++) {
                renderChunk(cx,cy);
            }
        }

        batch.end();

        renderEntities();
    }

    void renderEntities() {
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

    }

    public void linkToPlayer(Player player) {
        batch.setProjectionMatrix(player.getCamera().projection);
        entityBatch.setProjectionMatrix(player.getCamera().projection);
    }

    public Batch getEntityBatch() {
        return entityBatch;
    }
}
