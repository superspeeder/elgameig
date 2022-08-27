package org.delusion.elgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.menu.DebugUI;
import org.delusion.elgame.menu.Hotbar;
import org.delusion.elgame.utils.Toggleable;
import org.delusion.elgame.utils.Vector2i;

public class MainGameScreen extends ScreenAdapter {

    private final ElGame game;
    private final Toggleable<DebugUI> debugUI;
    private final SpriteBatch uibatch;
    private boolean leftClickingWorld = false;
    private boolean rightClickingWorld = false;
    private ShapeRenderer uishapes;

    public MainGameScreen(ElGame game) {
        this.game = game;
        uibatch = game.getUIBatch();
        debugUI = Toggleable.off(new DebugUI(game, uibatch));
        uishapes = new ShapeRenderer();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new MainGameInput(this));
        leftClickingWorld = false;
        rightClickingWorld = false;

    }

    public ElGame getGame() {
        return game;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);


        double sicp = game.getPlayer().getSpawninCooldownPercent();
        if (sicp > 0.0f) {

            float spc = (float) (1.0f  - sicp);

            game.getPlayer().updateC(delta);
            game.getWorld().render();

            uishapes.begin(ShapeRenderer.ShapeType.Filled);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            uishapes.setColor(0.0f, 0.0f, 0.0f, 0.6f);
            uishapes.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            uishapes.setColor(new Color(0x204a1faf));
            uishapes.circle(Gdx.graphics.getWidth() * 0.3f, Gdx.graphics.getHeight() / 2.f, Gdx.graphics.getWidth() * 0.025f);
            uishapes.circle(Gdx.graphics.getWidth() * 0.7f, Gdx.graphics.getHeight() / 2.f, Gdx.graphics.getWidth() * 0.025f);
            uishapes.rect(Gdx.graphics.getWidth() * 0.3f, Gdx.graphics.getHeight() * 0.5f - Gdx.graphics.getWidth() * 0.025f, Gdx.graphics.getWidth() * 0.4f, Gdx.graphics.getWidth() * 0.05f);

            uishapes.setColor(new Color(0x1ab017ff));
            uishapes.circle(Gdx.graphics.getWidth() * 0.3f, Gdx.graphics.getHeight() / 2.f, Gdx.graphics.getWidth() * 0.02f);
            uishapes.rect(Gdx.graphics.getWidth() * 0.3f, Gdx.graphics.getHeight() * 0.5f - Gdx.graphics.getWidth() * 0.02f, Gdx.graphics.getWidth() * 0.4f * spc, Gdx.graphics.getWidth() * 0.04f);
            uishapes.circle(Gdx.graphics.getWidth() * 0.3f + Gdx.graphics.getWidth() * 0.4f * spc, Gdx.graphics.getHeight() / 2.f, Gdx.graphics.getWidth() * 0.02f);

            uishapes.setColor(1.f, 1.f, 1.f, 1.f);

            uishapes.end();

            game.getPlayer().load();
            return;
        }

        if (delta > 1 / 30.0f) {
            delta = 1 / 30.0f;
        }

        if (leftClickingWorld) {
            Vector2i tp = game.getPlayer().tileFromScreenPos(Gdx.input.getX(), Gdx.input.getY());
            game.getPlayer().normalInteractWith(tp, false);
        }

        if (rightClickingWorld) {
            Vector2i tp = game.getPlayer().tileFromScreenPos(Gdx.input.getX(), Gdx.input.getY());
            game.getPlayer().secondaryInteractWith(tp, false);
        }


        game.getPlayer().update(delta);
        game.getWorld().render();

        uibatch.begin();
        getHotbar().render();


        debugUI.ifAccept(DebugUI::render);

        game.getPlayer().drawUIElements(uibatch);
        uibatch.end();

        uishapes.begin(ShapeRenderer.ShapeType.Line);
        game.getPlayer().drawUIShapesLine(uishapes);
        uishapes.end();
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

    public boolean onWorldClicked(int x, int y, int button) {
        Vector2i tp = game.getPlayer().tileFromScreenPos(x,y);

        if (button == Input.Buttons.LEFT) {
            leftClickingWorld = true;
            game.getPlayer().normalInteractWith(tp, true);
        } else if (button == Input.Buttons.RIGHT) {
            rightClickingWorld = true;
            game.getPlayer().secondaryInteractWith(tp, true);
        }

        return true;
    }

    public void onStopClicking(int x, int y, int button) {
        if (button == Input.Buttons.LEFT) {
            leftClickingWorld = false;
        } else if (button == Input.Buttons.RIGHT) {
            rightClickingWorld = false;
        }

    }

    public void onMouseMove(int screenX, int screenY) {
        game.getWorld().updateHoveredTile(game.getPlayer().tileFromScreenPos(screenX, screenY));
    }
}
