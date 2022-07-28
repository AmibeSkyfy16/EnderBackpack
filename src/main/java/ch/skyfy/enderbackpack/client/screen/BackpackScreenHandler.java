package ch.skyfy.enderbackpack.client.screen;

import ch.skyfy.enderbackpack.BackpackInventory;
import ch.skyfy.enderbackpack.EnderBackpack;
import ch.skyfy.enderbackpack.client.screen.slot.BackpackSlot;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BackpackScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    public final Byte row;

    public PacketByteBuf buf;

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        super(EnderBackpack.EXTENDED_SCREEN_HANDLER_TYPE, syncId);
        var stack = buf.readItemStack();
        this.buf = buf;
        row = (byte) buf.getInt(0);

        EnderBackpack.LOGGER.info("[BackpackScreenHandler.class]  env type is: "+FabricLoader.getInstance().getEnvironmentType().name()+", row is : " + row);


        inventory = new BackpackInventory(row * 9, stack, playerInventory.player.getUuidAsString());
        this.buildContainer(playerInventory);
        this.inventory.onOpen(playerInventory.player);
    }

    public void buildContainer(PlayerInventory playerInventory) {
        int i = (row - 4) * 18;
        int j;
        int k;
        for (j = 0; j < row; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new BackpackSlot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }
        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 161 + i));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }

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
