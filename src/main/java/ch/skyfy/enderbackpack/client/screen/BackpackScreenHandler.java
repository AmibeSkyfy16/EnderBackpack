package ch.skyfy.enderbackpack.client.screen;

import ch.skyfy.enderbackpack.BackpackInventory;
import ch.skyfy.enderbackpack.EnderBackpack;
import ch.skyfy.enderbackpack.client.screen.slot.BackpackSlot;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BackpackScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    public final int row;

    public PacketByteBuf buf;

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(EnderBackpack.EXTENDED_SCREEN_HANDLER_TYPE, syncId);
        this.buf = buf;
        row = buf.readVarInt();
        inventory = new BackpackInventory(row * 9, buf.readItemStack(), playerInventory.player.getUuidAsString());
        this.buildContainer(playerInventory);
        this.inventory.onOpen(playerInventory.player);
//        EnderBackpack.LOGGER.info("[BackpackScreenHandler.class]  env type is: "+FabricLoader.getInstance().getEnvironmentType().name()+", row is : " + row);
    }

    public void buildContainer(PlayerInventory playerInventory) {
        int i = (row - 4) * 18;
        int j;
        int k;
        for (j = 0; j < row; ++j) {
            for (k = 0; k < 9; ++k) {
                addSlot(new BackpackSlot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }
        for (j = 0; j < 9; ++j) {
            addSlot(new Slot(playerInventory, j, 8 + j * 18, 161 + i));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
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

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

//    @Override
//    public void close(PlayerEntity player) {
//        super.close(player);
//        this.inventory.onClose(player);
//    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    //    @Override
//    public ItemStack transferSlot(PlayerEntity player, int index) {
//        ItemStack stack = ItemStack.EMPTY;
//        Slot slot = this.getSlot(index);
//        if (slot != null && slot.hasStack()) {
//            final ItemStack stack2 = slot.getStack();
//            stack = stack2.copy();
//            if (index < this.inventory.size()) {
//                if (!this.insertItem(stack2, this.inventory.size(), this.slots.size(), true)) {
//                    return ItemStack.EMPTY;
//                }
//            } else if (!this.insertItem(stack2, 0, this.inventory.size(), false)) {
//                return ItemStack.EMPTY;
//            }
//            if (stack2.isEmpty()) {
//                slot.setStack(ItemStack.EMPTY);
//            } else {
//                slot.markDirty();
//            }
//        }
//        return stack;
//    }
}
