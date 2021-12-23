package ch.skyfy.singlebackpack.client;

import ch.skyfy.singlebackpack.SingleBackpack;
import ch.skyfy.singlebackpack.client.screen.BackpackScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class ClientSetup implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(SingleBackpack.BACKPACK_SCREEN_HANDLER, BackpackScreen::new);
    }
}
