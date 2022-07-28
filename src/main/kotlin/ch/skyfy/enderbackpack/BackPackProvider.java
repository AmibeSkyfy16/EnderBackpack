package ch.skyfy.enderbackpack;

import ch.skyfy.enderbackpack.client.screen.BackpackScreen;
import ch.skyfy.enderbackpack.client.screen.BackpackScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

public class BackPackProvider implements HandledScreens.Provider<BackpackScreenHandler, BackpackScreen>{

    @Override
    public void open(Text name, ScreenHandlerType<BackpackScreenHandler> type, MinecraftClient client, int id) {
        HandledScreens.Provider.super.open(name, type, client, id);
    }

    @Override
    public BackpackScreen create(BackpackScreenHandler handler, PlayerInventory playerInventory, Text title) {
        return new BackpackScreen(handler, playerInventory, title);
    }
}
