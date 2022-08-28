package org.delusion.elgame.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

public class TextElement extends BasicElement {
    private MsdfFont font;
    private String text;
    private FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();

    public void render(SpriteBatch batch) {
        this.font.draw(batch, this.text, this.getPosition().x, this.getPosition().y);
    }

    public String getText() {
        return this.text;
    }

    public void setColor(Color color) {
        this.param.color = color;
    }

    public void setText(String text) {
        this.text = text;
        GlyphLayout t = new GlyphLayout(this.font, text);
        this.setSize(new Vector2(t.width, t.height));
        this.setPosition(new Vector2((float) Gdx.graphics.getWidth() / 2.0F, (float)Gdx.graphics.getHeight() / 2.0F));
    }

    public TextElement(String text, FreeTypeFontGenerator gen) {
        this.param.size = 12;
        this.font = gen.generateFont(this.param);
        this.text = text;
        GlyphLayout t = new GlyphLayout(this.font, text);
        this.setSize(new Vector2(t.width, t.height));
        this.setPosition(new Vector2(((float)Gdx.graphics.getWidth() - this.getSize().x) / 2.0F, ((float)Gdx.graphics.getHeight() - this.getSize().y) / 2.0F));
    }

    public TextElement(String text, FreeTypeFontGenerator gen, Vector2 position, boolean centeredAtPosition) {
        this.param.size = 12;
        this.font = gen.generateFont(this.param);
        this.text = text;
        GlyphLayout t = new GlyphLayout(this.font, text);
        this.setSize(new Vector2(t.width, t.height));
        if (centeredAtPosition) {
            this.setPosition(new Vector2(position.x - this.getSize().x / 2.0F, position.y - this.getSize().x / 2.0F));
        } else {
            this.setPosition(position);
        }

    }

    public TextElement(String text, FreeTypeFontGenerator gen, int size) {
        this.param.size = size;
        this.font = gen.generateFont(this.param);
        this.text = text;
        GlyphLayout t = new GlyphLayout(this.font, text);
        this.setSize(new Vector2(t.width, t.height));
        this.setPosition(new Vector2(((float)Gdx.graphics.getWidth() - this.getSize().x) / 2.0F, ((float)Gdx.graphics.getHeight() - this.getSize().y) / 2.0F));
    }

    public TextElement(String text, FreeTypeFontGenerator gen, int size, Vector2 position, boolean centeredAtPosition) {
        this.param.size = size;
        this.font = gen.generateFont(this.param);
        this.text = text;
        GlyphLayout t = new GlyphLayout(this.font, text);
        this.setSize(new Vector2(t.width, t.height));
        if (centeredAtPosition) {
            this.setPosition(new Vector2(position.x - this.getSize().x / 2.0F, position.y - this.getSize().x / 2.0F));
        } else {
            this.setPosition(position);
        }

    }

    public TextElement(String text, FreeTypeFontGenerator gen, FreeTypeFontGenerator.FreeTypeFontParameter p) {
        this.param = p;
        this.font = gen.generateFont(this.param);
        this.text = text;
        GlyphLayout t = new GlyphLayout(this.font, text);
        this.setSize(new Vector2(t.width, t.height));
        this.setPosition(new Vector2(((float)Gdx.graphics.getWidth() - this.getSize().x) / 2.0F, ((float)Gdx.graphics.getHeight() - this.getSize().y) / 2.0F));
    }

    public TextElement(String text, FreeTypeFontGenerator gen, FreeTypeFontGenerator.FreeTypeFontParameter p, Vector2 position, boolean centeredAtPosition) {
        this.param = p;
        this.font = gen.generateFont(this.param);
        this.text = text;
        GlyphLayout t = new GlyphLayout(this.font, text);
        this.setSize(new Vector2(t.width, t.height));
        if (centeredAtPosition) {
            this.setPosition(new Vector2(position.x - this.getSize().x / 2.0F, position.y - this.getSize().x / 2.0F));
        } else {
            this.setPosition(position);
        }

    }
}
