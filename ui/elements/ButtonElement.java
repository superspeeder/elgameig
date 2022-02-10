package org.delusion.elgame.ui.elements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import org.delusion.elgame.ui.UIEvents;

import java.util.function.Consumer;

public class ButtonElement extends TextElement {
    private final Consumer<Integer> onClickFunction;
    private boolean hover = false;
    private int anim_frame;

    public void render(SpriteBatch batch) {
        super.render(batch);
//        batch.draw(MainMenu.atlas.findRegion("sprite", this.anim_frame), this.getPosition().x + this.getSize().x + 10.0F, this.getPosition().y - this.getSize().y, this.getSize().y, this.getSize().y);
        if (this.hover && this.anim_frame < 16) {
            ++this.anim_frame;
            ++this.anim_frame;
        } else if (!this.hover && this.anim_frame > 0) {
            --this.anim_frame;
            --this.anim_frame;
        }

        if (!this.hover && this.anim_frame <= 16 && this.anim_frame > 0) {
            --this.anim_frame;
            --this.anim_frame;
        }

    }

    public ButtonElement(String text, FreeTypeFontGenerator gen, Consumer<Integer> function) {
        super(text, gen);
        this.onClickFunction = function;
    }

    public ButtonElement(String text, FreeTypeFontGenerator gen, Vector2 position, boolean centeredAtPosition, Consumer<Integer> function) {
        super(text, gen, position, centeredAtPosition);
        this.onClickFunction = function;
    }

    public ButtonElement(String text, FreeTypeFontGenerator gen, int size, Consumer<Integer> function) {
        super(text, gen, size);
        this.onClickFunction = function;
    }

    public ButtonElement(String text, FreeTypeFontGenerator gen, int size, Vector2 position, boolean centeredAtPosition, Consumer<Integer> function) {
        super(text, gen, size, position, centeredAtPosition);
        this.onClickFunction = function;
    }

    public ButtonElement(String text, FreeTypeFontGenerator gen, FreeTypeFontGenerator.FreeTypeFontParameter p, Consumer<Integer> function) {
        super(text, gen, p);
        this.onClickFunction = function;
    }

    public ButtonElement(String text, FreeTypeFontGenerator gen, FreeTypeFontGenerator.FreeTypeFontParameter p, Vector2 position, boolean centeredAtPosition, Consumer<Integer> function) {
        super(text, gen, p, position, centeredAtPosition);
        this.onClickFunction = function;
    }

    public UIEvents[] wantedEvents() {
        return new UIEvents[]{UIEvents.CLICK_RELEASED, UIEvents.MOUSE_ENTER, UIEvents.MOUSE_LEAVE, UIEvents.FOCUS, UIEvents.UNFOCUS, UIEvents.PAGE_CLOSED};
    }

    public void onPageClosed() {
        super.onPageClosed();
        this.anim_frame = 0;
        this.hover = false;
    }

    public void onClickReleased(int button, Vector2 mousePos) {
        super.onClickReleased(button, mousePos);
        this.onClickFunction.accept(button);
    }

    public void onMouseEnter(Vector2 mousePos) {
        System.out.println("Enter");
        this.hover = true;
    }

    public void onMouseLeave(Vector2 mousePos) {
        System.out.println("Leave");
        this.hover = false;
    }
}
