package org.delusion.elgame.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.maltaisn.msdfgdx.FontStyle;
import com.maltaisn.msdfgdx.MsdfFont;
import org.delusion.elgame.item.Item;

import java.io.Serializable;

import static org.delusion.elgame.menu.DebugUI.msdfShader;

public class Stack implements Serializable {

    private Item item;
    private int count;

    private static MsdfFont font;
    private static FontStyle fontStyle = new FontStyle()
            .setFontName("Roboto")
            .setSize(24)
            .setColor(Color.WHITE)
            .setShadowColor(Color.DARK_GRAY);

    private static FontStyle fontStyleSmaller = new FontStyle()
            .setFontName("Roboto")
            .setSize(18)
            .setColor(Color.WHITE)
            .setShadowColor(Color.DARK_GRAY);


    private static BitmapFont bmfont;


    public static void load() {
//        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Regular.ttf"));
//        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
//        parameter.color = Color.DARK_GRAY;
//        parameter.borderColor = Color.LIGHT_GRAY;
//        parameter.borderWidth = 1.0f;
//        parameter.mono = true;
//        parameter.size = 24;
//        parameter.minFilter = Texture.TextureFilter.Linear;
//        parameter.magFilter = Texture.TextureFilter.Linear;
//
//        font = generator.generateFont(parameter);
//
//        FreeTypeFontGenerator.FreeTypeFontParameter parameter2 = new FreeTypeFontGenerator.FreeTypeFontParameter();
//        parameter2.color = Color.DARK_GRAY;
//        parameter2.borderColor = Color.LIGHT_GRAY;
//        parameter2.borderWidth = 1.0f;
//        parameter2.mono = true;
//        parameter2.size = 18;
//        parameter2.minFilter = Texture.TextureFilter.Linear;
//        parameter2.magFilter = Texture.TextureFilter.Linear;
//        fontSmaller = generator.generateFont(parameter2);

        font = new MsdfFont(Gdx.files.internal("fonts/msdf/Roboto-Regular/Roboto-Regular.fnt"), 32, 5);
        bmfont = font.getFont();
    }

    public Stack() {
        this(null, 0);
    }

    public Stack(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public Stack(Item item) {
        this(item, 1);
    }

    public Stack(Stack stack) {
        set(stack);
    }

    public Item getItem() {
        return item;
    }

    public Stack setItem(Item item) {
        this.item = item;
        return this;
    }

    public int getCount() {
        return count;
    }

    public Stack setCount(int count) {
        this.count = count;
        return this;
    }

    public Stack set(Stack stack) {
        this.item = stack.item;
        this.count = stack.count;
        return this;
    }

    public Stack copy() {
        return new Stack(this);
    }

    public Stack merge(Stack other) {
        if (item == null || count == 0) {
            set(other);
            return new Stack();
        }
        if (other.count == 0 || other.item == null || other.item != item || count == item.maxStackSize()) {
            return other;
        }

        if (other.count + count > item.maxStackSize()) {
            int cc = count;
            count = item.maxStackSize();
            return new Stack(other.item, other.count + cc - item.maxStackSize());
        }

        count += other.count;
        return new Stack();
    }

    public void render(SpriteBatch batch, int x, int y) {
        if (count > 0 && item != null) {
            batch.draw(item.getTex(), x, y, 64, 64);
            if (count > 1) {
                batch.setShader(msdfShader);
                bmfont.getData().setScale(fontStyle.getSize() / font.getGlyphSize());
                msdfShader.updateForFont(font, fontStyle);
                GlyphLayout gl = new GlyphLayout(bmfont, Integer.toString(count));
                bmfont.draw(batch, gl, x + 72 - gl.width, y + 16);
                batch.setShader(null);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("%s x %d", item.name, count);
    }

    public void add(int i) {
        count += i;
        if (count < 0) count = 0;
        if (count > item.maxStackSize()) count = item.maxStackSize();
    }

    public boolean isEmpty() {
        return item == null || count <= 0;
    }

    public boolean isFull() {
        return item != null && item.maxStackSize() == count;
    }

    public void renderAtCursor(SpriteBatch batch) {
        if (count > 0 && item != null) {
            batch.draw(item.getTex(), Gdx.input.getX() + 16, Gdx.graphics.getHeight() - Gdx.input.getY() - 48, 32, 32);
            if (count > 1) {
                batch.setShader(msdfShader);
                bmfont.getData().setScale(fontStyleSmaller.getSize() / font.getGlyphSize());
                msdfShader.updateForFont(font, fontStyleSmaller);
                GlyphLayout gl = new GlyphLayout(bmfont, Integer.toString(count));
                bmfont.draw(batch, gl, Gdx.input.getX() + 56 - gl.width, Gdx.graphics.getHeight() - Gdx.input.getY() - 40);
                batch.setShader(null);
            }
        }

//        if (count > 0 && item != null) {
//            batch.draw(item.getTex(), Gdx.input.getX() + 16, Gdx.graphics.getHeight() - Gdx.input.getY() - 48, 32, 32);
//            if (count > 1) {
//                GlyphLayout gl = new GlyphLayout(fontSmaller, Integer.toString(count));
//                fontSmaller.draw(batch, gl, Gdx.input.getX() + 72 - gl.width, Gdx.graphics.getHeight() - Gdx.input.getY() + 16);
//            }
//        }
    }
}
