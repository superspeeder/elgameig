package org.delusion.elgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import noise.OpenSimplexNoise;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.world.World;

public class MainGameScreen extends ScreenAdapter {

    private final ElGame game;

    public MainGameScreen(ElGame game) {
        this.game = game;
        Gdx.input.setInputProcessor(new MainGameInput(this));
    }


    public ElGame getGame() {
        return game;
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        game.getPlayer().update(delta);

        game.getWorld().render();
    }


    @Override
    public void dispose() {
    }
}
