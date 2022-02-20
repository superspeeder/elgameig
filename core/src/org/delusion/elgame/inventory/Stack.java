package org.delusion.elgame.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Stack {

    private Item item;
    private int count;
    private static BitmapFont font;

    public static void load() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.BLACK;
        parameter.borderColor = Color.WHITE;
        parameter.borderWidth = 1.0f;
        parameter.mono = true;
        parameter.size = 24;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;

        font = generator.generateFont(parameter);
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

    public Stack tryCombine(Stack other) {
        if (item == null || count == 0) {
            set(other);
            return new Stack();
        }
        if (other.count == 0 || other.item == null || other.item != item || count == item.maxStackSize) {
            return other;
        }

        if (other.count + count > item.maxStackSize) {
            int cc = count;
            count = item.maxStackSize;
            return new Stack(other.item, other.count + cc - item.maxStackSize);
        }

        count += other.count;
        return new Stack();
    }

    public void render(SpriteBatch batch, int x, int y) {
        if (count > 0 && item != null) {
            batch.draw(item.getTex(), x, y, 64, 64);
            if (count > 1) {
                GlyphLayout gl = new GlyphLayout(font, Integer.toString(count));
                font.draw(batch, gl, x + 72 - gl.width, y + 16);
            }
        }
    }
}
