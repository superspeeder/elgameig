package org.delusion.elgame.ui.elements;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.delusion.elgame.ui.UI;
import org.delusion.elgame.ui.UIEvents;

public abstract class BasicElement extends UIElement {
    private boolean focused = false;
    private boolean sendEventsOnFocusOnly = true;
    private Vector2 position = new Vector2(0.0F, 0.0F);
    private Vector2 size;
    private UI ui;
    private String page;
    private int index = -1;

    public BasicElement() {
    }

    public void onClick(int button, Vector2 mousePos) {
    }

    public void whileClicked(int button, Vector2 mousePos) {
    }

    public void onClickReleased(int button, Vector2 mousePos) {
    }

    public void focus() {
        this.focused = true;
    }

    public void unfocus() {
        this.focused = false;
    }

    public boolean isFocused() {
        return this.focused;
    }

    public void setSendKeyEventsFocusedOnly(boolean focusedOnly) {
        this.sendEventsOnFocusOnly = focusedOnly;
    }

    public boolean getSendKeyEventsFocusedOnly() {
        return this.sendEventsOnFocusOnly;
    }

    public void onKeyDown(int key) {
    }

    public void whileKeyPressed(int key) {
    }

    public void onKeyUp(int key) {
    }

    public void beforeEvents() {
    }

    public void onFocus() {
    }

    public void setParentUI(UI ui) {
        this.ui = ui;
    }

    public void setUIPage(String page) {
        this.page = page;
    }

    public UI getParentUI() {
        return this.ui;
    }

    public String getUIPage() {
        return this.page;
    }

    public int getIndexInPage() {
        if (this.index == -1 && this.ui != null) {
            this.index = this.ui.getIndexFor(this, this.getUIPage());
            if (this.index == -1) {
                return -1;
            }
        }

        if (this.hashCode() != this.ui.hashOfElementInPage(this.getUIPage(), this.index)) {
            this.index = -1;
            return this.getIndexInPage();
        } else {
            return this.index;
        }
    }

    public void onMouseEnter(Vector2 mousePos) {
    }

    public void onMouseLeave(Vector2 mousePos) {
    }

    public void onPageOpened() {
    }

    public void onPageClosed() {
    }

    public void onUnfocus() {
    }

    public UIEvents[] wantedEvents() {
        return new UIEvents[0];
    }

    public void onMouseOver(Vector2 mousePos) {
    }

    public Rectangle getElementBoundingBox() {
        return new Rectangle(this.position.x, this.position.y - this.size.y, this.size.x, this.size.y);
    }

    public void setPosition(Vector2 position) {
        this.position = position.cpy();
    }

    public void setSize(Vector2 size) {
        this.size = size.cpy();
    }

    public Vector2 getPosition() {
        return this.position.cpy();
    }

    public Vector2 getSize() {
        return this.size.cpy();
    }
}
