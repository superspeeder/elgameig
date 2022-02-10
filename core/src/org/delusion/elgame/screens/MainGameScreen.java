package org.delusion.elgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.menu.DebugUI;
import org.delusion.elgame.utils.Toggleable;

public class MainGameScreen extends ScreenAdapter {

    private final ElGame game;
    private final Toggleable<DebugUI> debugUI;

    public MainGameScreen(ElGame game) {
        this.game = game;
        debugUI = Toggleable.off(new DebugUI(game));
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
        game.getPlayer().update(delta);

        game.getWorld().render();
        debugUI.ifAccept(DebugUI::render);
    }


    @Override
    public void dispose() {
    }

    public Toggleable<DebugUI> getDebugUI() {
        return debugUI;
    }
}
