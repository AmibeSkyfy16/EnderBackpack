package ch.skyfy.enderbackpack.client.screen.slot;

import ch.skyfy.enderbackpack.BackpackItem;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class BackpackSlot extends Slot {

    public BackpackSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return (!((stack.getItem()) instanceof BackpackItem));
    }
}
