package ch.skyfy.singlebackpack.client;

import ch.skyfy.singlebackpack.SingleBackpack;
import ch.skyfy.singlebackpack.client.screen.BackpackScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@Environment(EnvType.CLIENT)
public class ClientSetup implements ClientModInitializer {

    public static String playerClientUUID;

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            System.out.println("CLIENT JOINED ");
            if(client.player != null) {
                playerClientUUID = client.player.getUuidAsString();
            }
        });
        ScreenRegistry.register(SingleBackpack.BACKPACK_SCREEN_HANDLER, BackpackScreen::new);
    }
}
