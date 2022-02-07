package org.delusion.elgame.screens;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;

public class MainGameInput extends InputAdapter {
    private final MainGameScreen mainGameScreen;

    public MainGameInput(MainGameScreen mainGameScreen) {
        this.mainGameScreen = mainGameScreen;
    }


}
