package org.delusion.elgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

public class MainGameInput extends InputAdapter {
    private final MainGameScreen mainGameScreen;

    public MainGameInput(MainGameScreen mainGameScreen) {
        this.mainGameScreen = mainGameScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.F3:
                mainGameScreen.getDebugUI().toggle();
                return true;
            case Input.Keys.F1:
                mainGameScreen.getGame().getSettings().advancedLightCascade = !mainGameScreen.getGame().getSettings().advancedLightCascade;
                return true;
            case Input.Keys.ESCAPE:
                mainGameScreen.getGame().setScreen(mainGameScreen.getGame().getMainMenuScreen());
                return true;
            case Input.Keys.NUM_1:

            case Input.Keys.NUMPAD_0:
                mainGameScreen.getHotbar().select(0);
                return true;
            case Input.Keys.NUM_2:
            case Input.Keys.NUMPAD_1:
                mainGameScreen.getHotbar().select(1);
                return true;
            case Input.Keys.NUM_3:
            case Input.Keys.NUMPAD_2:
                mainGameScreen.getHotbar().select(2);
                return true;
            case Input.Keys.NUM_4:
            case Input.Keys.NUMPAD_3:
                mainGameScreen.getHotbar().select(3);
                return true;
            case Input.Keys.NUM_5:
            case Input.Keys.NUMPAD_4:
                mainGameScreen.getHotbar().select(4);
                return true;
            case Input.Keys.NUM_6:
            case Input.Keys.NUMPAD_5:
                mainGameScreen.getHotbar().select(5);
                return true;
            case Input.Keys.NUM_7:
            case Input.Keys.NUMPAD_6:
                mainGameScreen.getHotbar().select(6);
                return true;
            case Input.Keys.NUM_8:
            case Input.Keys.NUMPAD_7:
                mainGameScreen.getHotbar().select(7);
                return true;
            case Input.Keys.NUM_9:
            case Input.Keys.NUMPAD_8:
                mainGameScreen.getHotbar().select(8);
                return true;
            case Input.Keys.NUM_0:
            case Input.Keys.NUMPAD_9:
                mainGameScreen.getHotbar().select(9);
                return true;
            case Input.Keys.E:
                mainGameScreen.getGame().setScreen(mainGameScreen.getGame().getInventoryScreen());
                return true;
            case Input.Keys.R:
                mainGameScreen.getGame().getPlayer().setPosition(new Vector2(0, 0));
                mainGameScreen.getGame().getPlayer().setVelocity(new Vector2(0, 0));
                mainGameScreen.getGame().getPlayer().setAcceleration(new Vector2(0, 0));
                return true;
            default:
                return super.keyDown(keycode);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (mainGameScreen.getHotbar().onClick(screenX,screenY,button)) {
            return true;
        }


        return mainGameScreen.onWorldClicked(screenX, screenY, button) ||  super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        mainGameScreen.onStopClicking(screenX, screenY, button);
        return super.touchUp(screenX, screenY, pointer, button);
    }
    //    @Override
//    public boolean touchDragged(int screenX, int screenY, int pointer) {
//        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
//            mainGameScreen.onWorldLeftClickDrag(screenX, screenY);
//            return true;
//        } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
//            mainGameScreen.onWorldRightClickDrag(screenX, screenY);
//            return true;
//        }
//
//        return super.touchDragged(screenX, screenY, pointer);
//    }



    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY == 1) {
            mainGameScreen.getHotbar().scrollRight();
            return true;
        } else if (amountY == -1) {
            mainGameScreen.getHotbar().scrollLeft();
            return true;
        }

        return super.scrolled(amountX, amountY);
    }
}
