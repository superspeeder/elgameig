package org.delusion.elgame.world;

import com.badlogic.gdx.utils.Disposable;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.utils.AABBi;
import org.delusion.elgame.utils.Vector2i;

public class Chunk implements Disposable {
    public static final int SIZE = 50;

    private Vector2i position;
    private AABBi bounds;
    private World world;
    private TileType[][] map;

    public Chunk(World world, Vector2i pos) {
        map = new TileType[SIZE][SIZE];

        position = pos;
        this.world = world;
        bounds = new AABBi(position.x, position.x + SIZE, position.y, position.y + SIZE);

        generate();
    }

    private void generate() {
        int localX = 0;
        for (int x = bounds.left ; x < bounds.right ; x++) {
            int localY = 0;
            for (int y = bounds.bottom ; y < bounds.top ; y++) {
                if (y < 10) {
                    set(localX, localY, TileType.Dirt);
                } else {
                    set(localX, localY, TileType.Air);
                }
                localY++;
            }
            localX++;
        }

    }

    public void set(int localX, int localY, TileType ttype) {
        map[localX][localY] = ttype;
    }

    public TileType get(int localX, int localY) {
        return map[localX][localY];
    }

    @Override
    public void dispose() {

    }

    public static void evictionListener(@Nullable Vector2i pos, @Nullable Chunk chunk, RemovalCause removalCause) {
        if (chunk != null) {
            chunk.dispose();
        }
    }
}
