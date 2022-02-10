package org.delusion.elgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import org.delusion.elgame.ui.elements.UIElement;

import java.util.*;

public class UI {
    private final UIInputProcessor inputProcessor;
    private SpriteBatch batch;
    public final FreeTypeFontGenerator uiFontGen;
    private boolean useBgTex = false;
    private Texture bgT = null;
    private Map<String, List<UIElement>> pages = new HashMap<>();
    private String curpage;

    public UI() {
        this.uiFontGen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ui.ttf"));
        this.batch = new SpriteBatch();
        this.inputProcessor = new UIInputProcessor();
    }

    public UI(String font) {
        this.uiFontGen = new FreeTypeFontGenerator(Gdx.files.internal(font));
        this.batch = new SpriteBatch();
        this.inputProcessor = new UIInputProcessor();
    }

    public void addElement(UIElement element, String page) {
        if (!this.pages.containsKey(page)) {
            this.addPage(page);
        }

        this.pages.get(page).add(element);
    }

    public void show() {
        Gdx.input.setInputProcessor(this.inputProcessor);
    }

    public void render() {
        this.batch.begin();
        if (this.useBgTex && this.bgT != null) {
            this.batch.draw(this.bgT, 0.0F, 0.0F, (float)Gdx.graphics.getWidth(), (float)Gdx.graphics.getHeight());
        }

        this.pages.get(this.curpage).forEach(e -> e.render(this.batch));

        this.batch.end();
    }

    public void updateInputs() {
        this.inputProcessor.loadListsFromPage(this.pages.get(this.curpage));
    }

    public void setBGTex(String s) {
        this.useBgTex = true;
        this.bgT = new Texture(Gdx.files.internal(s));
    }

    public void addPage(String name) {
        if (this.pages.keySet().isEmpty()) {
            this.curpage = name;
        }

        this.pages.put(name, new ArrayList<>());
    }

    public int getIndexFor(UIElement uiElement, String uiPage) {
        return !this.pages.containsKey(uiPage) ? -1 : this.pages.get(uiPage).indexOf(uiElement);
    }

    public int hashOfElementInPage(String uiPage, int index) {
        return this.pages.get(uiPage).get(index).hashCode();
    }

    public void setPage(String page) {
        this.curpage = page;
        this.updateInputs();
    }
}
