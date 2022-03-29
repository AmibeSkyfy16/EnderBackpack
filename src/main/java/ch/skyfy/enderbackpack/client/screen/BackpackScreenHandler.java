package ch.skyfy.enderbackpack.client.screen;

import ch.skyfy.enderbackpack.BackpacksManager;
import ch.skyfy.enderbackpack.EnderBackpack;
import ch.skyfy.enderbackpack.client.ClientSetup;
import ch.skyfy.enderbackpack.client.screen.slot.BackpackSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;


public class BackpackScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    private final Byte row;

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(BackpacksManager.playerRows.get(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? ClientSetup.playerClientUUID : playerInventory.player.getUuidAsString()) * 9));//9 * 6 slots
    }

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(EnderBackpack.BACKPACK_SCREEN_HANDLER, syncId);
        row = BackpacksManager.playerRows.get(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? ClientSetup.playerClientUUID : playerInventory.player.getUuidAsString());
        this.inventory = inventory;
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
