package org.delusion.elgame.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InventoryScreenInput extends InputAdapter {
    private final InventoryScreen inventoryScreen;

    public InventoryScreenInput(InventoryScreen inventoryScreen) {

        this.inventoryScreen = inventoryScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.E:
            case Input.Keys.ESCAPE:
                inventoryScreen.getGame().setScreen(inventoryScreen.getGame().getMainGameScreen());
                return true;
            default:
                return super.keyDown(keycode);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (inventoryScreen.getHotbar().onInvClick(screenX,screenY,button)) {
            return true;
        }
        if (inventoryScreen.getInventory().onInvClick(screenX,screenY,button)) {
            return true;
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY == -1) {
            inventoryScreen.getHotbar().scrollRight();
            return true;
        } else if (amountY == 1) {
            inventoryScreen.getHotbar().scrollLeft();
            return true;
        }


        return super.scrolled(amountX, amountY);
    }
}
