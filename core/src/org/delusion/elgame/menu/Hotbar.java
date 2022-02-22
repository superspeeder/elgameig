package org.delusion.elgame.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.delusion.elgame.inventory.IInventory;
import org.delusion.elgame.item.Items;
import org.delusion.elgame.inventory.Stack;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.utils.SimpleRenderable;

public class Hotbar implements IInventory, SimpleRenderable {

    private static final int SLOT_COUNT = 10;
    private static final int HOTBAR_SLOTSIZE = 96;
    private static final int HOTBAR_WIDTH = HOTBAR_SLOTSIZE * SLOT_COUNT;
    private final SpriteBatch batch;

    private HotbarSlot[] slots;
    private HotbarSlot curselected;
    private Player player;

    public Hotbar(SpriteBatch uiSpritebatch, Player player) {
        batch = uiSpritebatch;
        this.player = player;
        slots = new HotbarSlot[SLOT_COUNT];

        for (int i = 0 ; i < SLOT_COUNT ; i++) {
            String texname, selTexname;
            if (i == 0) {
                texname = "hotbarSlotLeft";
                selTexname = "selected/hotbarSlotLeftSelected";
            } else if (i == SLOT_COUNT - 1) {
                texname = "hotbarSlotRight";
                selTexname = "selected/hotbarSlotRightSelected";
            } else {
                texname = "hotbarSlotMiddle";
                selTexname = "selected/hotbarSlotMiddleSelected";
            }

            slots[i] = new HotbarSlot(texname, selTexname, i, this, new Stack());
        }

        select(0);

        slots[0].setStack(new Stack(Items.Pickaxe, 1));
        slots[1].setStack(new Stack(Items.Spade, 1));
        slots[2].setStack(new Stack(Items.SuperPickaxe, 1));
        slots[3].setStack(new Stack(Items.Torch, 99));
        slots[9].setStack(new Stack(Items.Grass, 200));
    }

    @Override
    public Slot[] getSlots() {
        return slots;
    }

    @Override
    public void render() {
        int starting_x = (Gdx.graphics.getWidth() - HOTBAR_WIDTH) / 2;
        int starting_y = (int) (Gdx.graphics.getHeight() - HOTBAR_SLOTSIZE * 1.25);
        for (int i = 0 ; i < slots.length ; i++) {
            slots[i].render(batch, starting_x + i * HOTBAR_SLOTSIZE, starting_y);
        }
    }

    public boolean onClick(int x, int y, int button) {
        int starting_x = (Gdx.graphics.getWidth() - HOTBAR_WIDTH) / 2;
        int starting_y = (int) (HOTBAR_SLOTSIZE * 0.25);
        int ending_x = starting_x + HOTBAR_WIDTH;
        int ending_y = starting_y + HOTBAR_SLOTSIZE;
        if (x >= starting_x && x <= ending_x && y >= starting_y & y <= ending_y) {
            if (button == Input.Buttons.LEFT) {
                int lx = x - starting_x;
                int sid = lx / HOTBAR_SLOTSIZE;

                select(sid);
            }
            return true;
        }
        return false;
    }

    public void select(int id) {
        if (curselected == null) {
            curselected = slots[id];
            slots[id].selected();
        } else if (curselected.getId() != id) {
            curselected.deselected();
            curselected = slots[id];
            slots[id].selected();
        }
    }

    public void scrollRight() {
        if (curselected.getId() == 9) {
            select(0);
        } else {
            select(curselected.getId() + 1);
        }
    }

    public void scrollLeft() {
        if (curselected.getId() == 0) {
            select(9);
        } else {
            select(curselected.getId() - 1);
        }
    }

    public boolean onInvClick(int x, int y, int button) {
        int starting_x = (Gdx.graphics.getWidth() - HOTBAR_WIDTH) / 2;
        int starting_y = (int) (HOTBAR_SLOTSIZE * 0.25);
        int ending_x = starting_x + HOTBAR_WIDTH;
        int ending_y = starting_y + HOTBAR_SLOTSIZE;
        if (x >= starting_x && x <= ending_x && y >= starting_y & y <= ending_y) {
            int lx = x - starting_x;
            int sid = lx / HOTBAR_SLOTSIZE;

            Slot s = slots[sid];
            s.onClick(x, y, button, player.getWorld().getGame());
            return true;
        }
        return false;
    }

    public Slot getSelected() {
        return curselected;
    }
}
