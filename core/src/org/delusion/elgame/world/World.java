package org.delusion.elgame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.tuple.Pair;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.tile.TileMetadata;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.tile.TileTypes;
import org.delusion.elgame.utils.AABBi;
import org.delusion.elgame.utils.SimpleRenderable;
import org.delusion.elgame.utils.Vector2i;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class World implements SimpleRenderable, Disposable {

    private ParallaxBackdrop backdrop;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public void reloadAll() {
        Set<Vector2i> ks = chunkCache.synchronous().asMap().keySet();
        chunkCache.synchronous().refreshAll(ks);
    }

    public void saveAll() {
        Set<Vector2i> ks = chunkCache.synchronous().asMap().keySet();
        try {
            chunkCache.getAll(ks).get().forEach((vector2i, chunk) -> chunk.save());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public boolean chunkIsAvaiable(Vector2i cpos) {
        return chunkCache.getIfPresent(cpos) != null;
    }

    public enum Layer {
        Main, BG
    }

    public static final int TILE_SIZE = 16;
    private static final long MAX_CHUNKS = 100;

    private final ElGame game;
    private final AsyncLoadingCache<Vector2i, Chunk> chunkCache = Caffeine.newBuilder()
            .maximumSize(MAX_CHUNKS)
            .removalListener(Chunk::removalListener)
            .buildAsync(pos -> new Chunk(this, pos));
    private final SpriteBatch batch;
    private final SpriteBatch entityBatch;

    private Player player;

    public World(ElGame game) {
        this.game = game;

        batch = new SpriteBatch();
        entityBatch = new SpriteBatch();

        Texture tex1 = new Texture(Gdx.files.internal("textures/backgrounds/forest_bd1.png"));
        Texture tex2 = new Texture(Gdx.files.internal("textures/backgrounds/forest_bd2.png"));
        tex1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
        tex2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
        backdrop = new ParallaxBackdrop(List.of(Pair.of(tex1, 1),Pair.of(tex2, 3)));
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

        chunkCache.get(chunkPos).thenAccept(chunk -> {
            chunk.set(localPos.x, localPos.y, ttype);
            chunk.updateBorder(x, y);
            chunk.recalculateLighting(localPos.x, localPos.y);
        });
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
        renderBackdrop();

        for (int cx = leftC ; cx <= rightC ; cx++) {
            for (int cy = bottomC ; cy <= topC ; cy++) {
                renderChunk(cx,cy);
            }
        }

        Vector2i tp = player.tileFromScreenPos(Gdx.input.getX(), Gdx.input.getY());
        batch.draw(player.getSelectionBox(), tp.x * World.TILE_SIZE, tp.y * World.TILE_SIZE, World.TILE_SIZE, World.TILE_SIZE);

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setTransformMatrix(game.getPlayer().getCamera().view);
        shapeRenderer.setProjectionMatrix(game.getPlayer().getCamera().projection);

        for (int cx = leftC ; cx <= rightC ; cx++) {
            for (int cy = bottomC ; cy <= topC ; cy++) {
                renderChunkBorders(cx,cy);
            }
        }

        shapeRenderer.end();

        renderEntities();
    }

    private void renderBackdrop() {
        backdrop.render(batch, game);
    }

    private void renderChunkBorders(int cx, int cy) {
        shapeRenderer.setColor(Color.BLACK);
        Chunk nc = chunkCache.get(new Vector2i(cx, cy)).getNow(null);
        if (nc != null) {
            if (!nc.isBordersMappedFirstTime()) {
                nc.recalculateBorders();
            }
            nc.renderBordersTo(shapeRenderer);
        }
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
            if (!nc.isLightingMappedFirstTime()) {
                nc.recalculateLighting();
            }
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

    public boolean canPlaceAt(Vector2i tilePos, TileType ttype) {
        return getTile(tilePos).getNow(null) == TileTypes.Air && !(ttype.getProperties().solid & collidesWithEntities(tilePos));
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

        chunkCache.get(chunkPos).thenAccept(chunk -> {
            chunk.setBg(localPos.x, localPos.y, ttype);
            chunk.recalculateLighting();
        });
    }

    public float getEmmittedLight(Vector2i tilePos) {
        TileType fgtt = getTileIfAvailable(tilePos);
        TileType bgtt = getTileBgIfAvailable(tilePos);

        if (fgtt == null || bgtt == null) {
            return -1.0f;
        }

        if (bgtt == TileTypes.Air && !fgtt.getProperties().solid && tilePos.y >= -25) { // sky behind
            return Math.max(fgtt.getProperties().emmission, 1.f);
        }

        if (fgtt.getProperties().emmission > 0.f) {
            return fgtt.getProperties().emmission;
        }

        return 0.f;
    }

    public TileType getTileBgIfAvailable(Vector2i tilePos) {
        return getTileBgIfAvailable(tilePos.x, tilePos.y);
    }

    public TileType getTileBgIfAvailable(int x, int y) {
        Vector2i chunkPos = new Vector2i(Math.floorDiv(x, Chunk.SIZE), Math.floorDiv(y, Chunk.SIZE));
        Vector2i localPos = new Vector2i(Math.floorMod(x, Chunk.SIZE), Math.floorMod(y, Chunk.SIZE));

        if (localPos.x < 0) {
            localPos.x = Chunk.SIZE - localPos.x;
        }

        if (localPos.y < 0) {
            localPos.y = Chunk.SIZE - localPos.y;
        }

        CompletableFuture<Chunk> c = chunkCache.getIfPresent(chunkPos);
        if (c == null) return null;
        try {
            return c.thenApply(ch -> ch.getBg(localPos)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public TileType getTileIfAvailable(Vector2i tilePos) {
        return getTileIfAvailable(tilePos.x, tilePos.y);
    }

    public TileType getTileIfAvailable(int x, int y) {
        Vector2i chunkPos = new Vector2i(Math.floorDiv(x, Chunk.SIZE), Math.floorDiv(y, Chunk.SIZE));
        Vector2i localPos = new Vector2i(Math.floorMod(x, Chunk.SIZE), Math.floorMod(y, Chunk.SIZE));

        if (localPos.x < 0) {
            localPos.x = Chunk.SIZE - localPos.x;
        }

        if (localPos.y < 0) {
            localPos.y = Chunk.SIZE - localPos.y;
        }

        CompletableFuture<Chunk> c = chunkCache.getIfPresent(chunkPos);
        if (c == null) return null;
        try {
            return c.thenApply(ch -> ch.get(localPos)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }


    public CompletableFuture<TileMetadata> getMetadata(int x, int y) {
        Vector2i chunkPos = new Vector2i(Math.floorDiv(x, Chunk.SIZE), Math.floorDiv(y, Chunk.SIZE));
        Vector2i localPos = new Vector2i(Math.floorMod(x, Chunk.SIZE), Math.floorMod(y, Chunk.SIZE));

        if (localPos.x < 0) {
            localPos.x = Chunk.SIZE - localPos.x;
        }

        if (localPos.y < 0) {
            localPos.y = Chunk.SIZE - localPos.y;
        }

        return chunkCache.get(chunkPos).thenApply(chunk -> chunk.getMetadata(localPos.x, localPos.y));
    }

    public CompletableFuture<TileMetadata> getMetadata(Vector2i tilePos) {
        return getMetadata(tilePos.x, tilePos.y);
    }


    public TileMetadata getMetadataIfAvailable(Vector2i tilePos) {
        return getMetadataIfAvailable(tilePos.x, tilePos.y);
    }

    public TileMetadata getMetadataIfAvailable(int x, int y) {
        Vector2i chunkPos = new Vector2i(Math.floorDiv(x, Chunk.SIZE), Math.floorDiv(y, Chunk.SIZE));
        Vector2i localPos = new Vector2i(Math.floorMod(x, Chunk.SIZE), Math.floorMod(y, Chunk.SIZE));

        if (localPos.x < 0) {
            localPos.x = Chunk.SIZE - localPos.x;
        }

        if (localPos.y < 0) {
            localPos.y = Chunk.SIZE - localPos.y;
        }

        CompletableFuture<Chunk> c = chunkCache.getIfPresent(chunkPos);
        if (c == null) return null;
        try {
            return c.thenApply(ch -> ch.getMetadata(localPos)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void recalculateLightIfAvailable(int x, int y) {
        recalculateLightIfAvailable(new Vector2i(x, y));
    }

    public void recalculateLightIfAvailable(Vector2i chunkPos) {
        CompletableFuture<Chunk> c = chunkCache.getIfPresent(chunkPos);
        if (c == null) return;
        c.thenAccept(Chunk::recalculateLighting);
    }

    public void recalculateLightNCIfAvailable(int x, int y) {
        recalculateLightNCIfAvailable(new Vector2i(x, y));
    }

    public void recalculateLightNCIfAvailable(Vector2i chunkPos) {
        CompletableFuture<Chunk> c = chunkCache.getIfPresent(chunkPos);
        if (c == null) return;
        c.thenAccept(Chunk::recalculateLightingNC);
    }

    public void recalculateLightCIfAvailable(int x, int y, int cls) {
        recalculateLightCIfAvailable(new Vector2i(x,y), cls);
    }

    public void recalculateLightCIfAvailable(Vector2i chunkPos, int cls) {
        CompletableFuture<Chunk> c = chunkCache.getIfPresent(chunkPos);
        if (c == null) return;
        c.thenAccept(chunk -> chunk.recalculateLightingC(false,cls));
    }

    public void dispose() {
        player.save();
        chunkCache.synchronous().invalidateAll();
        chunkCache.synchronous().cleanUp();
        System.out.println("Cleaned up cache");
    }
}