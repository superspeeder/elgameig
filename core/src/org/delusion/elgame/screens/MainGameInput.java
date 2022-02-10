package org.delusion.elgame.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class MainGameInput extends InputAdapter {
    private final MainGameScreen mainGameScreen;

    public MainGameInput(MainGameScreen mainGameScreen) {
        this.mainGameScreen = mainGameScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F3) {
            mainGameScreen.getDebugUI().toggle();
            return true;
        }
        if (keycode == Input.Keys.ESCAPE) {
            mainGameScreen.getGame().setScreen(mainGameScreen.getGame().getMainMenuScreen());
        }
        return super.keyDown(keycode);
    }
}
