package ch.skyfy.singlebackpack.client.screen;

import ch.skyfy.singlebackpack.SingleBackpack;
import ch.skyfy.singlebackpack.client.screen.slot.BackpackSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;


//the screen handler takes care of syncing the player inventory
//with the container
public class BackpackScreenHandler extends ScreenHandler {

    private final Inventory inventory;//your actual inventory

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(54));//9 * 6 slots
    }
    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(SingleBackpack.BACKPACK_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.buildContainer(playerInventory);
        this.inventory.onOpen(playerInventory.player);//calls onOpen() from our inventory to readNbt
    }

    //creates slots for the backpack
    public void buildContainer(PlayerInventory playerInventory) {
        //container
        for(int i = 0; i < 6; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new BackpackSlot(this.inventory, j + i  * 9, 8 + j * 18, 18 + i * 18));
            }
        }
        //player inventory
        int j;
        int k;
        for(j = 0; j < 3; ++j) {
            for(k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + 36));
            }
        }

        for(j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 161 + 36));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;//if this is false the player cannot access the screen
    }

    //calls the onClose() from our inventory to write nbt
    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }

    //Called when you try to insert an item from x to y inventory
    //this is a simple implementation to allow that
    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.getSlot(index);
        if (slot != null && slot.hasStack()) {
            final ItemStack stack2 = slot.getStack();
            stack = stack2.copy();
            if (index < this.inventory.size()) {
                if (!this.insertItem(stack2, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(stack2, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (stack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return stack;
    }

}
