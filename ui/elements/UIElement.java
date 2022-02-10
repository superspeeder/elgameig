package org.delusion.elgame.ui.elements;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.delusion.elgame.ui.UI;
import org.delusion.elgame.ui.UIEvents;

public abstract class UIElement {
    public UIElement() {
    }

    public abstract void render(SpriteBatch batch);

    public abstract void onClick(int button, Vector2 position);

    public abstract void whileClicked(int button, Vector2 position);

    public abstract void onClickReleased(int button, Vector2 position);

    public abstract void onFocus();

    public abstract void onUnfocus();

    public abstract void focus();

    public abstract void unfocus();

    public abstract boolean isFocused();

    public abstract void setSendKeyEventsFocusedOnly(boolean val);

    public abstract boolean getSendKeyEventsFocusedOnly();

    public abstract void onKeyDown(int key);

    public abstract void whileKeyPressed(int key);

    public abstract void onKeyUp(int key);

    public abstract void onMouseEnter(Vector2 position);

    public abstract void onMouseOver(Vector2 position);

    public abstract void onMouseLeave(Vector2 position);

    public abstract void onPageOpened();

    public abstract void onPageClosed();

    public abstract UIEvents[] wantedEvents();

    public abstract void beforeEvents();

    public abstract Rectangle getElementBoundingBox();

    public abstract void setPosition(Vector2 position);

    public abstract void setSize(Vector2 size);

    public abstract Vector2 getPosition();

    public abstract Vector2 getSize();

    public abstract void setParentUI(UI parent);

    public abstract void setUIPage(String name);

    public abstract UI getParentUI();

    public abstract String getUIPage();

    public abstract int getIndexInPage();
}
