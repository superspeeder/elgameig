package org.delusion.elgame.world;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.utils.SimpleRenderable;
import org.delusion.elgame.utils.Vector2i;

import java.util.concurrent.CompletableFuture;

public class World implements SimpleRenderable {

    private static final long MAX_CHUNKS = 100;

    private final ElGame game;
    private final AsyncLoadingCache<Vector2i, Chunk> chunkCache = Caffeine.newBuilder()
            .maximumSize(MAX_CHUNKS)
            .evictionListener(Chunk::evictionListener)
            .buildAsync(pos -> new Chunk(this, pos));

    public World(ElGame game) {
        this.game = game;
    }

    public CompletableFuture<TileType> getTile(int x, int y) {
        Vector2i chunkPos = new Vector2i(Math.floorDiv(x, Chunk.SIZE), Math.floorDiv(y, Chunk.SIZE));

    }


    @Override
    public void render() {

    }
}
