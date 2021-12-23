package ch.skyfy.singlebackpack;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;
import java.util.Map;

public class BackpackManager {

    public static Map<String, DefaultedList<ItemStack>> backpacks = new HashMap<>();

    public static void openInventory(PlayerEntity player) {
        var screenHandlerFactory = new SimpleNamedScreenHandlerFactory((syncId, inv, player1) -> {
            return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, player.getInventory(), new ImplementedInventory() {
                @Override
                public DefaultedList<ItemStack> getItems() {
                    if (backpacks.containsKey(player.getUuidAsString())) return backpacks.get(player.getUuidAsString());
                    return DefaultedList.ofSize(54, ItemStack.EMPTY);
                }
            }, 6);
        }, Text.of("backpack"));
        player.openHandledScreen(screenHandlerFactory);

    }

}
