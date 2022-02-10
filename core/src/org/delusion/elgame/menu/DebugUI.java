package org.delusion.elgame.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.utils.SimpleRenderable;
import org.delusion.elgame.world.World;

public class DebugUI implements SimpleRenderable {

    private final SpriteBatch batch;
    private final BitmapFont font;
    private final ElGame game;

    public DebugUI(ElGame game) {
        this.game = game;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.WHITE;
        parameter.mono = true;
        parameter.size = 18;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        font = generator.generateFont(parameter);
        batch = new SpriteBatch();
    }

    @Override
    public void render() {

        batch.begin();
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, String.format("Position: (%.3f, %.3f)", game.getPlayer().getPosition().x / (float) World.TILE_SIZE, game.getPlayer().getPosition().y / (float) World.TILE_SIZE), 20, Gdx.graphics.getHeight() - 40);
        batch.end();
    }
}
