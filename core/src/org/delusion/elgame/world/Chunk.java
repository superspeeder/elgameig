package org.delusion.elgame.world;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.github.benmanes.caffeine.cache.RemovalCause;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.delusion.elgame.tile.TileMetadata;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.tile.TileTypes;
import org.delusion.elgame.utils.AABBi;
import org.delusion.elgame.utils.Vector2i;

public class Chunk implements Disposable {
    public static final int SIZE = 50;
    public static final float LIGHT_AIRSTEP = 0.05f;
    public static final float LIGHT_SOLIDSTEP = 0.1f;

    private Vector2i position;
    private AABBi bounds;
    private World world;
    private TileType[][] map;
    private TileType[][] backgroundTilemap;
    private TileMetadata[][] metadataMap;
    private boolean lightingMappedFirstTime = false;

    public Chunk(World world, Vector2i pos) {
        map = new TileType[SIZE][SIZE];
        backgroundTilemap = new TileType[SIZE][SIZE];
        metadataMap = new TileMetadata[SIZE][SIZE];

        position = pos;
        this.world = world;
        bounds = new AABBi(position.x * SIZE, position.x * SIZE + SIZE, position.y * SIZE, position.y * SIZE + SIZE);

        generate();
    }

    private void generate() {
        int localX = 0;
        for (int x = bounds.left ; x < bounds.right ; x++) {
            int localY = 0;
            int h = 0;

            for (int y = bounds.bottom ; y < bounds.top ; y++) {
                metadataMap[localX][localY] = new TileMetadata();
                metadataMap[localX][localY].lightValue = 0.0f;

                if (y < h - 20) {
                    setBg(localX, localY, TileTypes.Stone);
                } else if (y < h - 1) {
                    setBg(localX, localY, TileTypes.Dirt);
                } else if (y < h) {
                    setBg(localX, localY, TileTypes.Grass);
                } else {
                    setBg(localX, localY, TileTypes.Air);
                }

                if (x > 50 && x < 54) {
                    set(localX, localY, TileTypes.Air);
                } else if (y < h - 20) {
                    set(localX, localY, TileTypes.Stone);
                } else if (y < h - 1) {
                    set(localX, localY, TileTypes.Dirt);
                } else if (y < h) {
                    set(localX, localY, TileTypes.Grass);
                } else if (y == 14) {
                    set(localX, localY, TileTypes.StoneTile);
                } else if (y == 15 && x % 5 == 0) {
                    set(localX, localY, TileTypes.StoneTile);
                } else {
                    set(localX, localY, TileTypes.Air);
                }
                localY++;
            }
            localX++;
        }
    }

    public void setBg(int localX, int localY, TileType type) {
        backgroundTilemap[localX][localY] = type;
    }

    public TileType getBg(int localX, int localY) {
        return backgroundTilemap[localX][localY];
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

    public void renderTo(SpriteBatch batch) {
        for (int x = 0 ; x < SIZE ; x++) {
            for (int y = 0 ; y < SIZE ; y++) {
                getBg(x,y).renderToBG(batch, x + position.x * SIZE, y + position.y * SIZE, world, getMetadata(x, y));
            }
        }
        for (int x = 0 ; x < SIZE ; x++) {
            for (int y = 0 ; y < SIZE ; y++) {
                get(x,y).renderTo(batch, x + position.x * SIZE, y + position.y * SIZE, world, getMetadata(x, y));
            }
        }
    }

    public TileMetadata getMetadata(int x, int y) {
        return metadataMap[x][y];
    }

    public void recalculateLighting() {
        recalculateLightingInternal(true, false);
    }

    public void recalculateLightingNC() {
        if (lightingMappedFirstTime) {
            recalculateLightingInternal(false, true);
        }
    }

    public void recalculateLightingInternal(boolean cbk, boolean trytokeep) {
        Queue<Vector2i> pendingWorldPositions = new LinkedList<>();

        for (int x = 0 ; x < SIZE ; x++) {
            for (int y = 0 ; y < SIZE ; y++) {
                if (!trytokeep) {
                    float el = world.getEmmittedLight(new Vector2i(x + position.x * SIZE, y + position.y * SIZE));
                    if (el > 0.0f) {
                        metadataMap[x][y].lightValue = el;
                        pendingWorldPositions.add(new Vector2i(x + position.x * SIZE, y + position.y * SIZE));
                    } else {
                        metadataMap[x][y].lightValue = 0.0f;
                    }
                } else {
                    float el = world.getEmmittedLight(new Vector2i(x + position.x * SIZE, y + position.y * SIZE));
                    if (el > 0.0f) {
                        metadataMap[x][y].lightValue = el;
                        pendingWorldPositions.add(new Vector2i(x + position.x * SIZE, y + position.y * SIZE));
                    } else if (metadataMap[x][y].lightValue > 0.0f) {
                        pendingWorldPositions.add(new Vector2i(x + position.x * SIZE, y + position.y * SIZE));
                    }
                }
            }
        }

        if (cbk) {
            world.recalculateLightNCIfAvailable(position.x - 1, position.y);
            world.recalculateLightNCIfAvailable(position.x + 1, position.y);
            world.recalculateLightNCIfAvailable(position.x, position.y - 1);
            world.recalculateLightNCIfAvailable(position.x, position.y + 1);

            world.recalculateLightNCIfAvailable(position.x - 1, position.y - 1);
            world.recalculateLightNCIfAvailable(position.x + 1, position.y - 1);
            world.recalculateLightNCIfAvailable(position.x - 1, position.y + 1);
            world.recalculateLightNCIfAvailable(position.x + 1, position.y + 1);
        }

        int iters = 0;
        while (!pendingWorldPositions.isEmpty()) {
            Vector2i curPos = pendingWorldPositions.remove();
            TileType tt = world.getTileIfAvailable(curPos);
            TileMetadata tm = world.getMetadataIfAvailable(curPos);
            if(tt == null) {
                continue;
            }
            boolean csolid = tt != TileTypes.Air;

            {
                Vector2i npos = new Vector2i(curPos.x - 1, curPos.y);
                TileType tt2 = world.getTileIfAvailable(npos);
                if (tt2 != null) {
                    boolean solid = tt2 != TileTypes.Air;
                    TileMetadata tm2 = world.getMetadataIfAvailable(npos);
                    if (!solid) {
                        float nll = Math.max(0.0f,tm.lightValue - LIGHT_AIRSTEP);
                        if (nll > tm2.lightValue) {
                            tm2.lightValue = nll;
                            if (nll > 0) {
                                pendingWorldPositions.add(npos);
                            }
                        }
                    } else {
                        float nll = Math.max(0.0f,tm.lightValue - LIGHT_SOLIDSTEP);
                        if (nll > tm2.lightValue) {
                            tm2.lightValue = nll;
                            if (nll > 0) {
                                pendingWorldPositions.add(npos);
                            }
                        }
                    }
                }
            }

            {
                Vector2i npos = new Vector2i(curPos.x + 1, curPos.y);
                TileType tt2 = world.getTileIfAvailable(npos);
                if (tt2 != null) {
                    boolean solid = tt2 != TileTypes.Air;
                    TileMetadata tm2 = world.getMetadataIfAvailable(npos);
                    if (!solid) {
                        float nll = Math.max(0.0f,tm.lightValue - LIGHT_AIRSTEP);
                        if (nll > tm2.lightValue) {
                            tm2.lightValue = nll;
                            if (nll > 0) {
                                pendingWorldPositions.add(npos);
                            }
                        }
                    } else {
                        float nll = Math.max(0.0f,tm.lightValue - LIGHT_SOLIDSTEP);
                        if (nll > tm2.lightValue) {
                            tm2.lightValue = nll;
                            if (nll > 0) {
                                pendingWorldPositions.add(npos);
                            }
                        }
                    }
                }
            }

            {
                Vector2i npos = new Vector2i(curPos.x, curPos.y - 1);
                TileType tt2 = world.getTileIfAvailable(npos);
                if (tt2 != null) {
                    boolean solid = tt2 != TileTypes.Air;
                    TileMetadata tm2 = world.getMetadataIfAvailable(npos);
                    if (!solid) {
                        float nll = Math.max(0.0f,tm.lightValue - LIGHT_AIRSTEP);
                        if (nll > tm2.lightValue) {
                            tm2.lightValue = nll;
                            if (nll > 0) {
                                pendingWorldPositions.add(npos);
                            }
                        }
                    } else {
                        float nll = Math.max(0.0f,tm.lightValue - LIGHT_SOLIDSTEP);
                        if (nll > tm2.lightValue) {
                            tm2.lightValue = nll;
                            if (nll > 0) {
                                pendingWorldPositions.add(npos);
                            }
                        }
                    }
                }
            }

            {
                Vector2i npos = new Vector2i(curPos.x, curPos.y + 1);
                TileType tt2 = world.getTileIfAvailable(npos);
                if (tt2 != null) {
                    boolean solid = tt2 != TileTypes.Air;
                    TileMetadata tm2 = world.getMetadataIfAvailable(npos);
                    if (!solid) {
                        float nll = Math.max(0.0f,tm.lightValue - LIGHT_AIRSTEP);
                        if (nll > tm2.lightValue) {
                            tm2.lightValue = nll;
                            if (nll > 0) {
                                pendingWorldPositions.add(npos);
                            }
                        }
                    } else {
                        float nll = Math.max(0.0f,tm.lightValue - LIGHT_SOLIDSTEP);
                        if (nll > tm2.lightValue) {
                            tm2.lightValue = nll;
                            if (nll > 0) {
                                pendingWorldPositions.add(npos);
                            }
                        }
                    }
                }
            }
            iters++;
        }

        lightingMappedFirstTime = true;
    }

//    private void calcLightFor(Vector2i tpos, Queue<Vector2i> pendingWorldPositions) {
//        TileMetadata meta = world.getMetadataIfAvailable(tpos);
//        float el = world.getEmmittedLight(tpos);
//        if (el != 0.0f) {
//            continue;
//        }
//
//    }

    public TileType get(Vector2i localPos) {
        return get(localPos.x, localPos.y);
    }

    public TileType getBg(Vector2i localPos) {
        return getBg(localPos.x, localPos.y);
    }

    public TileMetadata getMetadata(Vector2i localPos) {
        return getMetadata(localPos.x, localPos.y);
    }

    public boolean isLightingMappedFirstTime() {
        return lightingMappedFirstTime;
    }
}
