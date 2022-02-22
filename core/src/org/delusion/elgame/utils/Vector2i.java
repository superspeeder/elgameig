package org.delusion.elgame.utils;

import com.badlogic.gdx.math.Vector2;
import org.delusion.elgame.world.Chunk;
import org.delusion.elgame.world.World;

public class Vector2i {
    public int x, y;

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2i worldToChunk(Vector2 position) {
        return new Vector2i(Math.floorDiv((int)position.x, World.TILE_SIZE * Chunk.SIZE),Math.floorDiv((int)position.y, World.TILE_SIZE * Chunk.SIZE));
    }

    public static Vector2i worldToChunk(Vector2i position) {
        return new Vector2i(Math.floorDiv(position.x, World.TILE_SIZE * Chunk.SIZE),Math.floorDiv(position.y, World.TILE_SIZE * Chunk.SIZE));
    }

    public static Vector2i tileToChunk(Vector2 position) {
        return new Vector2i(Math.floorDiv((int)position.x, Chunk.SIZE),Math.floorDiv((int)position.y, Chunk.SIZE));
    }

    public static Vector2i tileToChunk(Vector2i position) {
        return new Vector2i(Math.floorDiv(position.x, Chunk.SIZE),Math.floorDiv(position.y, Chunk.SIZE));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2i vector2i = (Vector2i) o;

        if (x != vector2i.x) return false;
        return y == vector2i.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "Vector2i{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
