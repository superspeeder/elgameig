package org.delusion.elgame.inventory;

import org.delusion.elgame.menu.Slot;

public interface IInventory {

    Slot[] getSlots();

    default Stack tryMergeExisting(Stack stack) {
        for (Slot s : getSlots()) {
            if (s.isEmpty()) continue;
            stack = s.tryMerge(stack);
        }
        return stack;
    }

    default Stack tryInsert(Stack stack) {
        for (Slot s : getSlots()) {
            stack = s.tryMerge(stack);
        }
        return stack;
    }
}
