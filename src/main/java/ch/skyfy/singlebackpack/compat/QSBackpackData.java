package ch.skyfy.singlebackpack.compat;

import ch.skyfy.singlebackpack.BackpackInventory;
import ch.skyfy.singlebackpack.BackpacksManager;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
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
