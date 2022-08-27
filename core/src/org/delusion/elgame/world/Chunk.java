package org.delusion.elgame.world;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.github.benmanes.caffeine.cache.RemovalCause;
import noise.OpenSimplexNoise;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.delusion.elgame.data.DataManager;
import org.delusion.elgame.item.Item;
import org.delusion.elgame.tile.TileMetadata;
import org.delusion.elgame.tile.TileType;
import org.delusion.elgame.tile.TileTypes;
import org.delusion.elgame.utils.AABBi;
import org.delusion.elgame.utils.Vector2i;

public class Chunk implements Disposable {
    private static final int BORDER_BIT_LEFT = 0b1;
    private static final int BORDER_BIT_RIGHT = 0b10;
    private static final int BORDER_BIT_TOP = 0b100;
    private static final int BORDER_BIT_BOTTOM = 0b1000;


    public static final int SIZE = 16;
    public static final float LIGHT_AIRSTEP = 0.05f;
    public static final float LIGHT_SOLIDSTEP = 0.1f;
    private static final float BORDER_LINE_WIDTH = 1.0f;

    private Vector2i position;
    private AABBi bounds;
    private World world;
    private TileType[][] map;
    private TileType[][] backgroundTilemap;
    private TileMetadata[][] metadataMap;
    private float[][] breakProgressForeground;
    private float[][] breakProgressBackground;

    private boolean lightingMappedFirstTime = false;
    private boolean bordersMappedFirstTime = false;

    private static OpenSimplexNoise NOISE = new OpenSimplexNoise();

    public Chunk(World world, Vector2i pos) {
        map = new TileType[SIZE][SIZE];
        backgroundTilemap = new TileType[SIZE][SIZE];
        metadataMap = new TileMetadata[SIZE][SIZE];
        breakProgressForeground = new float[SIZE][SIZE];
        breakProgressBackground = new float[SIZE][SIZE];

        position = pos;
        this.world = world;
        bounds = new AABBi(position.x * SIZE, position.x * SIZE + SIZE, position.y * SIZE, position.y * SIZE + SIZE);

        load();
    }

    private void generate() {
        int localX = 0;
        for (int x = bounds.left ; x < bounds.right ; x++) {
            int localY = 0;
            int h = 0;

            for (int y = bounds.bottom ; y < bounds.top ; y++) {
                metadataMap[localX][localY] = new TileMetadata();
                metadataMap[localX][localY].lightValue = 0.0f;


                double v = 1;
                if (world.getGame().getSettings().experimentalCaveGen) v = Math.abs(NOISE.eval(x / 30.0f, y / 30.0f));

                if (y < -80) {
                    setBg(localX, localY, TileTypes.Air);
                } else if (y < h - 20) {
                    setBg(localX, localY, TileTypes.Stone);
                } else if (y < h - 1) {
                    setBg(localX, localY, TileTypes.Dirt);
                } else if (y < h) {
                    setBg(localX, localY, TileTypes.Grass);
                } else {
                    setBg(localX, localY, TileTypes.Air);
                }


                if (world.getGame().getSettings().experimentalCaveGen && v > 0.5) {
                    set(localX, localY, TileTypes.Air);
                } else if (x > 50 && x < 54) {
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

    public boolean isBordersMappedFirstTime() {
        return bordersMappedFirstTime;
    }

    public void recalculateBorders() {
        for (int x = bounds.left - 1 ; x <= bounds.right ; x++) {
            for (int y = bounds.bottom - 1 ; y <= bounds.top ; y++) {
                updateBorder(x, y);
            }
        }

        bordersMappedFirstTime = true;
    }

    public void setBg(int localX, int localY, TileType type) {
        backgroundTilemap[localX][localY] = type;
        breakProgressBackground[localX][localY] = 0.0f;
    }

    public TileType getBg(int localX, int localY) {
        return backgroundTilemap[localX][localY];
    }

    public void set(int localX, int localY, TileType ttype) {
        map[localX][localY] = ttype;
        breakProgressForeground[localX][localY] = 0.0f;
    }

    public TileType get(int localX, int localY) {
        return map[localX][localY];
    }

    @Override
    public void dispose() {
        save();
    }

    public static void removalListener(@Nullable Vector2i pos, @Nullable Chunk chunk, RemovalCause removalCause) {
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
        recalculateLightingInternal(true, false, 0);
    }

    public void recalculateLightingNC() {
        if (lightingMappedFirstTime) {
            recalculateLightingInternal(false, true, 0);
        }
    }

    public void recalculateLightingInternal(boolean cbk, boolean trytokeep, int cascadingLayers) {
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
            if (cascadingLayers > 0) {
                world.recalculateLightCIfAvailable(position.x - 1, position.y, cascadingLayers - 1);
                world.recalculateLightCIfAvailable(position.x + 1, position.y, cascadingLayers - 1);
                world.recalculateLightCIfAvailable(position.x, position.y - 1, cascadingLayers - 1);
                world.recalculateLightCIfAvailable(position.x, position.y + 1, cascadingLayers - 1);

                world.recalculateLightCIfAvailable(position.x - 1, position.y - 1, cascadingLayers - 1);
                world.recalculateLightCIfAvailable(position.x + 1, position.y - 1, cascadingLayers - 1);
                world.recalculateLightCIfAvailable(position.x - 1, position.y + 1, cascadingLayers - 1);
                world.recalculateLightCIfAvailable(position.x + 1, position.y + 1, cascadingLayers - 1);
            } else {
                world.recalculateLightNCIfAvailable(position.x - 1, position.y);
                world.recalculateLightNCIfAvailable(position.x + 1, position.y);
                world.recalculateLightNCIfAvailable(position.x, position.y - 1);
                world.recalculateLightNCIfAvailable(position.x, position.y + 1);

                world.recalculateLightNCIfAvailable(position.x - 1, position.y - 1);
                world.recalculateLightNCIfAvailable(position.x + 1, position.y - 1);
                world.recalculateLightNCIfAvailable(position.x - 1, position.y + 1);
                world.recalculateLightNCIfAvailable(position.x + 1, position.y + 1);
            }
        }

        int iters = 0;
        while (!pendingWorldPositions.isEmpty()) {
            Vector2i curPos = pendingWorldPositions.remove();
            TileType tt = world.getTileIfAvailable(curPos);
            TileMetadata tm = world.getMetadataIfAvailable(curPos);
            if(tt == null) {
                continue;
            }
            boolean csolid = tt.getProperties().solid;

            {
                Vector2i npos = new Vector2i(curPos.x - 1, curPos.y);
                TileType tt2 = world.getTileIfAvailable(npos);
                if (tt2 != null) {
                    boolean solid = tt2.getProperties().solid;
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
                    boolean solid = tt2.getProperties().solid;
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
                    boolean solid = tt2.getProperties().solid;
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
                    boolean solid = tt2.getProperties().solid;
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

    public void updateBorder(int x, int y) {
        TileMetadata tm = world.getMetadataIfAvailable(x, y);
        TileType tt = world.getTileIfAvailable(x, y);

        if (tm == null || tt == null) return;

        { // left
            TileType tt2 = world.getTileIfAvailable(x - 1, y);
            if (tt2 != null) {
                if (tt.getProperties().solid == tt2.getProperties().solid) {
                    tm.border ^= tm.border & BORDER_BIT_LEFT;
                    TileMetadata tm2 = world.getMetadataIfAvailable(x - 1, y);
                    if (tm2 != null) {
                        tm2.border ^= tm2.border & BORDER_BIT_RIGHT;
                    }
                } else {
                    tm.border |= BORDER_BIT_LEFT;
                }
            }
        }

        { // right
            TileType tt2 = world.getTileIfAvailable(x + 1, y);
            if (tt2 != null) {
                if (tt.getProperties().solid == tt2.getProperties().solid) {
                    tm.border ^= tm.border & BORDER_BIT_RIGHT;
                    TileMetadata tm2 = world.getMetadataIfAvailable(x + 1, y);
                    if (tm2 != null) {
                        tm2.border ^= tm2.border & BORDER_BIT_LEFT;
                    }
                } else {
                    tm.border |= BORDER_BIT_RIGHT;
                }
            }
        }

        { // top
            TileType tt2 = world.getTileIfAvailable(x , y + 1);
            if (tt2 != null) {
                if (tt.getProperties().solid == tt2.getProperties().solid) {
                    tm.border ^= tm.border & BORDER_BIT_TOP;
                    TileMetadata tm2 = world.getMetadataIfAvailable(x, y + 1);
                    if (tm2 != null) {
                        tm2.border ^= tm2.border & BORDER_BIT_BOTTOM;
                    }
                } else {
                    tm.border |= BORDER_BIT_TOP;
                }
            }
        }

        { // bottom
            TileType tt2 = world.getTileIfAvailable(x, y - 1);
            if (tt2 != null) {
                if (tt.getProperties().solid == tt2.getProperties().solid) {
                    tm.border ^= tm.border & BORDER_BIT_BOTTOM;
                    TileMetadata tm2 = world.getMetadataIfAvailable(x, y - 1);
                    if (tm2 != null) {
                        tm2.border ^= tm2.border & BORDER_BIT_TOP;
                    }
                } else {
                    tm.border |= BORDER_BIT_BOTTOM;
                }
            }
        }
    }

    public void renderBordersTo(ShapeRenderer shapeRenderer) {
        for (int x = 0 ; x < SIZE ; x++) {
            for (int y = 0 ; y < SIZE ; y++) {
                TileMetadata m = metadataMap[x][y];
                renderBordersAt(shapeRenderer, m.border, x + position.x * SIZE, y + position.y * SIZE);
            }
        }
    }

    private void renderBordersAt(ShapeRenderer shapeRenderer, byte border, int x, int y) {
        if ((border & BORDER_BIT_BOTTOM) != 0) {
            shapeRenderer.rectLine(x * World.TILE_SIZE, y * World.TILE_SIZE, (x + 1) * World.TILE_SIZE, y * World.TILE_SIZE, BORDER_LINE_WIDTH);
        }
        if ((border & BORDER_BIT_TOP) != 0) {
            shapeRenderer.rectLine(x * World.TILE_SIZE, (y + 1) * World.TILE_SIZE, (x + 1) * World.TILE_SIZE, (y + 1) * World.TILE_SIZE, BORDER_LINE_WIDTH);
        }
        if ((border & BORDER_BIT_LEFT) != 0) {
            shapeRenderer.rectLine(x * World.TILE_SIZE, y * World.TILE_SIZE, x * World.TILE_SIZE, (y + 1) * World.TILE_SIZE, BORDER_LINE_WIDTH);
        }
        if ((border & BORDER_BIT_RIGHT) != 0) {
            shapeRenderer.rectLine((x + 1) * World.TILE_SIZE, y * World.TILE_SIZE, (x + 1) * World.TILE_SIZE, (y + 1) * World.TILE_SIZE, BORDER_LINE_WIDTH);
        }
    }

    public void recalculateLighting(int x, int y) {
        if (world.getGame().getSettings().advancedLightCascade && (x <= 4 || x >= SIZE - 4 || y <= 4 || y >= SIZE - 4)) {
            recalculateLightingC(false,1);
        } else {
            recalculateLighting();
        }
    }

    public void recalculateLightingC(boolean tryKeep, int cls) {
        recalculateLightingInternal(true,tryKeep,cls);
    }

    public void save() {
        if (!DataManager.canSave()) return;
        Path fpath = DataManager.ROOT_STORAGE_PATH.resolve(String.format("chunks/%d_%d.chunk", position.x, position.y));


        ByteBuffer bb = ByteBuffer.allocate(SIZE * SIZE * 2 * Integer.BYTES); // WORLD_SAVE_SIZE
        for (int x = 0 ; x < SIZE ; x++) {
            for (int y = 0 ; y < SIZE ; y++) {
                bb.putInt(get(x, y).getId());
                bb.putInt(getBg(x, y).getId());
            }
        }

        File sfile = fpath.toFile();

        if (!sfile.exists()) {
            try {
                File dir = fpath.getParent().toFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                sfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileOutputStream stream = new FileOutputStream(sfile)) {
            stream.getChannel().write(bb.rewind());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Saved chunk %d, %d\n", position.x, position.y);

    }

    public void load() {
        Path fpath = DataManager.ROOT_STORAGE_PATH.resolve(String.format("chunks/%d_%d.chunk", position.x, position.y));
        File sfile = fpath.toFile();
        if (!sfile.exists()) {
            generate();
            save();
            return;
        }
        try (FileInputStream stream = new FileInputStream(sfile)) {
            FileChannel channel = stream.getChannel();
            ByteBuffer bb = ByteBuffer.allocate((int) channel.size());
            channel.read(bb);
            bb.rewind();
            for (int x = 0 ; x < SIZE ; x++) {
                for (int y = 0 ; y < SIZE ; y++) {
                    if (bb.remaining() < 8) {
                        System.err.println("Warning: Broken chunk data file, not finishing load");
                        return;
                    }
                    map[x][y] = TileType.fromId(bb.getInt());
                    backgroundTilemap[x][y] = TileType.fromId(bb.getInt());
                    metadataMap[x][y] = new TileMetadata();
                    metadataMap[x][y].lightValue = 0.0f;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Loaded chunk %d, %d\n", position.x, position.y);
    }

    public void damageTile(Vector2i pos, float toolSpeed, Item item, World.Layer layer) {
        switch (layer) {
            case Main -> {
                if (!item.canBreak(get(pos))) return;
                breakProgressForeground[pos.x][pos.y] += toolSpeed;
            }
            case BG -> {
                if (!item.canBreak(getBg(pos))) return;
                breakProgressBackground[pos.x][pos.y] += toolSpeed;
            }
        }
    }

    public float getDamage(Vector2i localPos, World.Layer layer) {
        switch (layer) {
            case Main -> {
                return breakProgressForeground[localPos.x][localPos.y];
            }
            case BG -> {
                return breakProgressBackground[localPos.x][localPos.y];
            }
        }
        return 0.0f;
    }

    public void renderShapeOverlayTo(ShapeRenderer shapeRenderer) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (int x = 0 ; x < SIZE ; x++) {
            for (int y = 0; y < SIZE; y++) {
                TileType tt = map[x][y];
                float maxB, b;
                if (tt != null && tt != TileTypes.Air) {
                    maxB = tt.getProperties().breakingHealth * 2.0f;
                    b = breakProgressForeground[x][y];
                    shapeRenderer.setColor(0.8f, 0.0f, 0.0f, b / maxB);
                } else {
                    tt = backgroundTilemap[x][y];
                    if (tt != null && tt != TileTypes.Air) {
                        maxB = tt.getProperties().breakingHealth * 2.0f;
                        b = breakProgressBackground[x][y];
                        shapeRenderer.setColor(0.6f, 0.0f, 0.0f, b / maxB);
                    } else continue;
                }
                shapeRenderer.rect((x + position.x * SIZE) * World.TILE_SIZE + 2, (y + position.y * SIZE) * World.TILE_SIZE + 2, World.TILE_SIZE - 4, World.TILE_SIZE - 4);
            }
        }
    }

    public void renderTexOverlayTo(SpriteBatch batch) {

    }
}
