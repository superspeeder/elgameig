package org.delusion.elgame.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.delusion.elgame.menu.Slot;
import org.delusion.elgame.player.Player;
import org.delusion.elgame.screens.InventoryScreen;
import org.delusion.elgame.utils.SimpleRenderable;

public class PlayerInventory implements IInventory, SimpleRenderable {

    private static final int COLS = 10;
    private static final int ROWS = 8;

    private static final int SLOT_SIZE = 96;
    private static final int SPACING = 8;
    private static final int TOTAL_WIDTH = COLS * (SLOT_SIZE + SPACING) - SPACING;
    private static final int TOTAL_HEIGHT = ROWS * (SLOT_SIZE + SPACING) - SPACING;
    private static final int YDFTOP = 96 * 3;

    private Slot[] slots;
    private SpriteBatch batch;
    private Player player;

    public PlayerInventory(SpriteBatch batch, Player plr) {
        this.batch = batch;
        player = plr;

        slots = new Slot[COLS * ROWS];

        for (int y = 0 ; y < ROWS ; y++) {
            for (int x = 0 ; x < COLS ; x++) {
                slots[y * COLS + x] = new Slot("slotDefault", y * COLS + x, new Stack());
            }
        }

        slots[13].setStack(new Stack(Item.Stone, 123));
        slots[16].setStack(new Stack(Item.Dirt, 200));
        slots[15].setStack(new Stack(Item.Dirt, 123));
    }


    @Override
    public Slot[] getSlots() {
        return slots;
    }

    @Override
    public void render() {
        int starting_x = (Gdx.graphics.getWidth() - TOTAL_WIDTH) / 2;
        int starting_y = Gdx.graphics.getHeight() - (YDFTOP + TOTAL_HEIGHT);
        for (int y = 0 ; y < ROWS ; y++) {
            for (int x = 0 ; x < COLS ; x++) {
                slots[y * COLS + x].render(batch, starting_x + x * (SLOT_SIZE + SPACING), starting_y + (ROWS - y) * (SLOT_SIZE + SPACING));
            }
        }
    }

    public boolean onInvClick(int x, int y, int button) {
        int starting_x = (Gdx.graphics.getWidth() - TOTAL_WIDTH) / 2;
        int starting_y = YDFTOP - (SLOT_SIZE + SPACING);
        int ending_x = starting_x + TOTAL_WIDTH;
        int ending_y = starting_y + TOTAL_HEIGHT;

        if (x >= starting_x && x <= ending_x && y >= starting_y & y <= ending_y) {
            int lx = x - starting_x;
            int ly = y - starting_y;
            int sx = lx / (SLOT_SIZE + SPACING);
            int sy = ly / (SLOT_SIZE + SPACING);
            int sid = sy * COLS + sx;
            if (Math.floorMod(lx, SLOT_SIZE + SPACING) <= SLOT_SIZE && Math.floorMod(ly, SLOT_SIZE + SPACING) <= SLOT_SIZE) {
                Slot s = slots[sid];
                s.onClick(x,y,button,player.getWorld().getGame());
                return true;
            }
        }
        return false;
    }
}
