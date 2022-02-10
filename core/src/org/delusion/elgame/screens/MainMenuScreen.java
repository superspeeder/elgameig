package org.delusion.elgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import org.delusion.elgame.ElGame;

public class MainMenuScreen extends ScreenAdapter {
    private final ElGame game;
    private MainMenuButton playGameButton;
    private MainMenuButton exitButton;
    private Container<VerticalGroup> group;
    private FreeTypeFontGenerator fontGen;
    private Stage stage;

    static class MainMenuButton extends Container<TextButton> {
        static BitmapFont font;
        static NinePatch buttonTex, buttonTexActive;
        static NinePatchDrawable buttonTexActiveDrawable;
        static NinePatchDrawable buttonTexDrawable;
        static TextButton.TextButtonStyle tbskin = new TextButton.TextButtonStyle();

        static void init(FreeTypeFontGenerator fontGenerator) {
            FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
            fontParam.color = Color.WHITE;
            fontParam.size = 32;
            fontParam.minFilter = Texture.TextureFilter.Linear;
            fontParam.magFilter = Texture.TextureFilter.Linear;
            font = fontGenerator.generateFont(fontParam);
        }

        public MainMenuButton(String text) {
            buttonTex = new NinePatch(new Texture(Gdx.files.internal("textures/ui/buttonNormal.png")), 8, 8, 8, 8);
            buttonTexActive = new NinePatch(new Texture(Gdx.files.internal("textures/ui/buttonActive.png")), 8, 8, 8, 8);

            buttonTexActiveDrawable = new NinePatchDrawable(buttonTexActive);
            buttonTexDrawable = new NinePatchDrawable(buttonTex);

            tbskin.down = buttonTexActiveDrawable;
            tbskin.fontColor = Color.WHITE;
            tbskin.font = font;
            tbskin.up = buttonTexDrawable;
            tbskin.over = buttonTexActiveDrawable;
            GlyphLayout glyt = new GlyphLayout(font, text);

            setActor(new TextButton(text, tbskin));
            minWidth(glyt.width * 3.f);
            prefHeight(glyt.height * 1.8f);
//            maxSize(glyt.width * 3.f, glyt.height * 1.8f);
            maxHeight(glyt.height * 1.8f);
            minHeight(glyt.height * 1.8f);
            fill();

            pad(4);
        }

        public MainMenuButton(String text, ChangeListener onClick) {
            this(text);
            getActor().addListener(onClick);
        }
    }

    public MainMenuScreen(ElGame game) {
        this.game = game;
        stage = new Stage();
//        stage.setDebugAll(true);
        fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Regular.ttf"));

        group = new Container<>(new VerticalGroup());
//        group.debug();
        group.getActor().center().bottom();
        group.getActor().fill();
        group.maxWidth(stage.getWidth() * 0.6666666666666667f);
        group.maxHeight(stage.getHeight());
        group.width(stage.getWidth() * 0.2f);
        group.pad(4);
        stage.addActor(group);

        MainMenuButton.init(fontGen);

        playGameButton = new MainMenuButton("Play", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getMainGameScreen());
            }
        });

        exitButton = new MainMenuButton("Exit", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });


        group.getActor().addActor(playGameButton);
        group.getActor().addActor(exitButton);

        group.setFillParent(true);
        group.center().align(Align.center).pad(24);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        ScreenUtils.clear(Color.BLACK);
        stage.act();
        stage.draw();

    }
}
