package org.delusion.elgame.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import org.delusion.elgame.ElGame;
import org.delusion.elgame.inventory.Stack;

public class Slot {

    protected static TextureAtlas slotAtlas;

    protected TextureRegion tex;
    private final int id;
    private Stack stack;

    public Slot(String texname, int id, Stack currentStack) {
        tex = getSlotAtlas().findRegion(texname);
        this.id = id;
        stack = currentStack;
    }

    public static void load() {
        slotAtlas = new TextureAtlas(Gdx.files.internal("textures/ui/slotAtlas.atlas"));
        slotAtlas.getRegions().forEach((atlasRegion -> {
            System.out.println(atlasRegion.name + " " + atlasRegion.index);
        }));
    }

    public static TextureAtlas getSlotAtlas() {
        return slotAtlas;
    }

    public void render(SpriteBatch batch, int x, int y) {
        batch.draw(tex, x, y);
        stack.render(batch, x + 16, y + 16);
    }

    public int getId() {
        return id;
    }

    public void setStack(Stack stack) {
        this.stack = stack;
    }

    public void onClick(int x, int y, int button, ElGame game) {
        if (button == Input.Buttons.LEFT) {
            Stack hand = game.getInventoryScreen().getStackInHand();
            if (hand.getItem() == null || hand.getCount() == 0) {
                if (stack.getCount() > 0 && stack.getItem() != null) {
                    game.getInventoryScreen().setStackInHand(stack);
                    setStack(new Stack());
                    return;
                } else {
                    return;
                }
            }

            if (hand.getItem() != stack.getItem() || stack.getCount() == stack.getItem().maxStackSize()) {
                game.getInventoryScreen().setStackInHand(stack);
                stack = hand;
                return;
            }

            game.getInventoryScreen().setStackInHand(stack.merge(hand));

        } else if (button == Input.Buttons.RIGHT) {
            Stack hand = game.getInventoryScreen().getStackInHand();
            if (hand.getItem() == null || hand.getCount() == 0) {
                if (stack.getCount() > 0 && stack.getItem() != null) {
                    int taking = stack.getCount() / 2;
                    game.getInventoryScreen().setStackInHand(new Stack(stack.getItem(), taking));
                    setStack(new Stack(stack.getItem(), stack.getCount() - taking));
                    return;
                } else {
                    return;
                }
            }

            if (stack.getCount() == 0) {
                stack.setItem(hand.getItem()).setCount(1);
                game.getInventoryScreen().setStackInHand(hand.copy().setCount(hand.getCount() - 1));
                return;
            }

            if (hand.getItem() != stack.getItem() || stack.getCount() == stack.getItem().maxStackSize()) {
                return;
            }

            stack.setCount(stack.getCount() + 1);
            game.getInventoryScreen().setStackInHand(hand.copy().setCount(hand.getCount() - 1));

        }
    }

    public Stack getStack() {
        return stack;
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public Stack tryMerge(Stack other) {
//        if (stack.isEmpty()) {
//            setStack(other);
//            return new Stack();
//        }
//        if (stack.getItem() == other.getItem()) {
//            if (!stack.isFull()) {
//
//            }
//        }
        return stack.merge(other);
    }
}
