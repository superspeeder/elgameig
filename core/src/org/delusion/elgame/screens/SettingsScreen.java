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
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import org.delusion.elgame.ElGame;

public class SettingsScreen extends ScreenAdapter {
    private final ElGame game;
    private final SettingsCheckBox experimentalCaveGenToggle;
    private FreeTypeFontGenerator fontGen;
    private Container<VerticalGroup> group;
    private Stage stage;
    private SettingsCheckBox advancedLightingToggle;
    private MainMenuScreen.MainMenuButton returnButton;

    static class SettingsCheckBox extends Container<CheckBox> {
        static BitmapFont font;
        static NinePatch cbTex, cbTexActive, cbTexOver, cbTexOverActive;
        static NinePatchDrawable cbTexActiveDrawable;
        static NinePatchDrawable cbTexDrawable;
        static NinePatchDrawable cbTexOverDrawable;
        static NinePatchDrawable cbTexOverActiveDrawable;
        static CheckBox.CheckBoxStyle cbskin = new CheckBox.CheckBoxStyle();

        static void init(FreeTypeFontGenerator fontGenerator) {
            FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
            fontParam.color = Color.WHITE;
            fontParam.size = 32;
            fontParam.minFilter = Texture.TextureFilter.Linear;
            fontParam.magFilter = Texture.TextureFilter.Linear;
            font = fontGenerator.generateFont(fontParam);
        }

        public SettingsCheckBox(String text) {
            cbTex = new NinePatch(new Texture(Gdx.files.internal("textures/ui/buttonNormal.png")), 8, 8, 8, 8);
            cbTexActive = new NinePatch(new Texture(Gdx.files.internal("textures/ui/boxChecked.png")), 8, 8, 8, 8);
            cbTexOver = new NinePatch(new Texture(Gdx.files.internal("textures/ui/buttonActive.png")), 8, 8, 8, 8);
            cbTexOverActive = new NinePatch(new Texture(Gdx.files.internal("textures/ui/boxActiveWithCheck.png")), 8, 8, 8, 8);


            cbTexActiveDrawable = new NinePatchDrawable(cbTexActive);
            cbTexDrawable = new NinePatchDrawable(cbTex);
            cbTexOverDrawable = new NinePatchDrawable(cbTexOver);
            cbTexOverActiveDrawable = new NinePatchDrawable(cbTexOverActive);

            cbskin.checkboxOn = cbTexActiveDrawable;
            cbskin.fontColor = Color.WHITE;
            cbskin.font = font;
            cbskin.checkboxOff = cbTexDrawable;
            cbskin.checkboxOver = cbTexOverDrawable;
            cbskin.checkboxOnOver = cbTexOverActiveDrawable;
            GlyphLayout glyt = new GlyphLayout(font, text);

            setActor(new CheckBox(text, cbskin));
            minWidth(glyt.width);
            minHeight(glyt.height * 1.2f);
            prefHeight(glyt.height * 1.2f);
//            maxSize(glyt.width, glyt.height * .6f);
            maxHeight(glyt.height * 1.2f);
            minHeight(glyt.height * 1.8f);
            fill();

            getActor().getImage().setScaling(Scaling.fill);
            getActor().getImageCell().size(glyt.height * 2.2f);


            pad(10);
        }

        public SettingsCheckBox(String text, ChangeListener onClick) {
            this(text);
            getActor().addListener(onClick);
        }
    }


    public SettingsScreen(ElGame game) {
        this.game = game;
        stage = new Stage();

        Image bg = new Image(new Texture("textures/backgrounds/menu.png"));
        bg.setPosition(0, 0);
        bg.setSize(stage.getWidth(), stage.getHeight());
        bg.setColor(MainMenuScreen.BGTINT);
        stage.addActor(bg);

        fontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Regular.ttf"));

        group = new Container<>(new VerticalGroup());
        group.getActor().center().bottom();
        group.getActor().fill();
        group.maxWidth(stage.getWidth() * 0.6666666666666667f);
        group.maxHeight(stage.getHeight());
        group.width(stage.getWidth() * 0.2f);
        group.pad(4);
        stage.addActor(group);

        SettingsScreen.SettingsCheckBox.init(fontGen);

        advancedLightingToggle = new SettingsScreen.SettingsCheckBox("Advanced Lighting", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getSettings().advancedLightCascade = ((CheckBox)actor).isChecked();
            }
        });
        advancedLightingToggle.getActor().setChecked(game.getSettings().advancedLightCascade);

        experimentalCaveGenToggle = new SettingsScreen.SettingsCheckBox("Experimental Cave Generation", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getSettings().experimentalCaveGen = ((CheckBox)actor).isChecked();
            }
        });
        experimentalCaveGenToggle.getActor().setChecked(game.getSettings().experimentalCaveGen);


        returnButton = new MainMenuScreen.MainMenuButton("Return", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.getMainMenuScreen());
            }
        });

        group.getActor().addActor(advancedLightingToggle);
        group.getActor().addActor(experimentalCaveGenToggle);
        group.getActor().addActor(returnButton);

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
