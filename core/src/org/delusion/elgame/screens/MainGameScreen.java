package org.delusion.elgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.menu.DebugUI;
import org.delusion.elgame.menu.Hotbar;
import org.delusion.elgame.utils.Toggleable;

public class MainGameScreen extends ScreenAdapter {

    private final ElGame game;
    private final Toggleable<DebugUI> debugUI;
    private final SpriteBatch uibatch;


    public MainGameScreen(ElGame game) {
        this.game = game;
        uibatch = game.getUIBatch();
        debugUI = Toggleable.off(new DebugUI(game, uibatch));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new MainGameInput(this));
    }

    public ElGame getGame() {
        return game;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        if (delta > 1 / 30.0f) {
            delta = 1 / 30.0f;
        }

        game.getPlayer().update(delta);
        game.getWorld().render();

        uibatch.begin();
        getHotbar().render();


        debugUI.ifAccept(DebugUI::render);
        uibatch.end();
    }


    @Override
    public void dispose() {
    }

    public Toggleable<DebugUI> getDebugUI() {
        return debugUI;
    }

    public Hotbar getHotbar() {
        return getGame().getPlayer().getHotbar();
    }
}
