package org.delusion.elgame.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.utils.SimpleRenderable;
import org.delusion.elgame.utils.Vector2i;
import org.delusion.elgame.world.Chunk;
import org.delusion.elgame.world.World;

public class DebugUI implements SimpleRenderable {

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ElGame game;

    public DebugUI(ElGame game, SpriteBatch uibatch) {
        this.game = game;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;
        parameter.mono = true;
        parameter.size = 18;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        font = generator.generateFont(parameter);
        batch = uibatch;
    }

    @Override
    public void render() {
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, String.format("Position: (%.3f, %.3f)", game.getPlayer().getPosition().x / (float) World.TILE_SIZE, game.getPlayer().getPosition().y / (float) World.TILE_SIZE), 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, String.format("Velocity: (%.3f, %.3f)", game.getPlayer().getVelocity().x / (float) World.TILE_SIZE, game.getPlayer().getVelocity().y / (float) World.TILE_SIZE), 20, Gdx.graphics.getHeight() - 60);
        Vector2i tp = game.getPlayer().tileFromScreenPos(Gdx.input.getX(), Gdx.input.getY());
        font.draw(batch, String.format("Hovered Tile: (%d, %d)", tp.x, tp.y), 20, Gdx.graphics.getHeight() - 80);
        font.draw(batch, String.format("Zoom: %f", game.getPlayer().getZoom()), 20, Gdx.graphics.getHeight() - 100);
        font.draw(batch, String.format("Current Chunk: (%d,%d)", Math.floorDiv((int) game.getPlayer().getPosition().x, World.TILE_SIZE * Chunk.SIZE), Math.floorDiv((int) game.getPlayer().getPosition().y , World.TILE_SIZE * Chunk.SIZE)), 20, Gdx.graphics.getHeight() - 120);
        font.draw(batch, String.format("Current Chunk Surface Temperature: (%f)", game.getWorld().getChunkOrNull(Math.floorDiv((int) game.getPlayer().getPosition().x, World.TILE_SIZE * Chunk.SIZE), Math.floorDiv((int) game.getPlayer().getPosition().y, World.TILE_SIZE * Chunk.SIZE)).getHistoricTemperature()), 20, Gdx.graphics.getHeight() - 140);
        font.draw(batch, String.format("Current Chunk Pressure: (%f)", game.getWorld().getChunkOrNull(Math.floorDiv((int) game.getPlayer().getPosition().x, World.TILE_SIZE * Chunk.SIZE), Math.floorDiv((int) game.getPlayer().getPosition().y, World.TILE_SIZE * Chunk.SIZE)).getHistoricPressure()), 20, Gdx.graphics.getHeight() - 160);
    }
}
