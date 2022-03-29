package ch.skyfy.enderbackpack.compat;

import ch.skyfy.enderbackpack.BackpackInventory;
import ch.skyfy.enderbackpack.BackpacksManager;
import net.kyrptonaught.quickshulker.api.QuickShulkerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class QSBackpackData extends QuickShulkerData {

    @Override
    public Inventory getInventory(PlayerEntity player, ItemStack stack) {
        return new BackpackInventory(BackpacksManager.playerRows.get(player.getUuidAsString()) * 9, stack, player.getUuidAsString()); //gets the Inventory
    }
}
