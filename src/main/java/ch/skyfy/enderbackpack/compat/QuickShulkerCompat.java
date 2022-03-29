package ch.skyfy.enderbackpack.compat;

import ch.skyfy.enderbackpack.BackpackItem;
import net.kyrptonaught.quickshulker.api.QuickOpenableRegistry;
import net.kyrptonaught.quickshulker.api.RegisterQuickShulker;

public class QuickShulkerCompat implements RegisterQuickShulker {

    @Override
    public void registerProviders() {
        QSBackpackData backpackData = new QSBackpackData();
        new QuickOpenableRegistry.Builder(backpackData)
                .setItem(BackpackItem.class)
                .supportsBundleing(true)// should the item support bundling like insert/extract
                .ignoreSingleStackCheck(false) //should the single stack requirement be ignored? If set to false, the item can only be used if it's a single item in a stack
                .setOpenAction((player, itemStack) -> {
                    player.openHandledScreen(BackpackItem.createScreenHandler(player, itemStack));
                }).register();
    }
}
