package org.delusion.elgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import noise.OpenSimplexNoise;
import org.delusion.elgame.ElGame;

public class MainGameScreen extends ScreenAdapter {

    private final ElGame game;
    public MainGameScreen(ElGame game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
    }


    @Override
    public void dispose() {
    }
}
