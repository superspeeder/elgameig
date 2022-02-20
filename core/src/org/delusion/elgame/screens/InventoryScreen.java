package org.delusion.elgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.inventory.PlayerInventory;
import org.delusion.elgame.inventory.Stack;
import org.delusion.elgame.menu.Hotbar;

public class InventoryScreen extends ScreenAdapter {
    private final ElGame game;
    private final InventoryScreenInput input;
    private ShapeRenderer sr;
    private Stack stackInHand = new Stack();

    public ElGame getGame() {
        return game;
    }

    public InventoryScreen(ElGame game) {
        this.game = game;
        input = new InventoryScreenInput(this);
        sr = new ShapeRenderer();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.getWorld().render();

        sr.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setColor(0.0f, 0.0f, 0.0f, 0.6f);
        sr.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        sr.end();

        game.getUIBatch().begin();
        getHotbar().render();
        getInventory().render();
        if (stackInHand.getItem() != null && stackInHand.getCount() > 0) {
            stackInHand.render(game.getUIBatch(), Gdx.input.getX() - 24, Gdx.graphics.getHeight() - Gdx.input.getY() - 24);
        }

        game.getUIBatch().end();
    }

    public Stack getStackInHand() {
        return stackInHand;
    }

    public InventoryScreen setStackInHand(Stack stackInHand) {
        this.stackInHand = stackInHand;
        return this;
    }

    public PlayerInventory getInventory() {
        return game.getPlayer().getInventory();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(input);
    }

    public Hotbar getHotbar() {
        return game.getPlayer().getHotbar();
    }
}
