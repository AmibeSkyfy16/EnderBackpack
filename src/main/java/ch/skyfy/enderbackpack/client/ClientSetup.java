package ch.skyfy.enderbackpack.client;

import ch.skyfy.enderbackpack.EnderBackpack;
import ch.skyfy.enderbackpack.client.screen.BackpackScreen;
import ch.skyfy.enderbackpack.feature.PlayerTimeMeter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

@Environment(EnvType.CLIENT)
public class ClientSetup implements ClientModInitializer {

    public static String playerClientUUID = "";

    @Override
    public void onInitializeClient() {
        // When the client will join a dedicated server, we will register his uuid for a future usage
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null)
                playerClientUUID = client.player.getUuidAsString();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> playerClientUUID = "");
        //noinspection deprecation
        ScreenRegistry.register(EnderBackpack.BACKPACK_SCREEN_HANDLER, BackpackScreen::new);

        registerEvents();
    }

    /**
     * Registers some events when a player start and join a singleplayer world, so a timer is started to calculate player time
     */
    private void registerEvents() {
        var playerTimes = PlayerTimeMeter.getInstance().playerTimes;

        // Start timer when start a singleplayer world
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (!server.isDedicated())
                PlayerTimeMeter.getInstance().startSaverTimer();
        });

        // Stop timer when stopping a singleplayer world
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            if (!server.isDedicated()) {
                System.out.println("SinglePlayer server stopped");
                PlayerTimeMeter.getInstance().stopSaverTimer();
                playerTimes.forEach(PlayerTimeMeter.PlayerTime::saveTimeToDisk);
                playerTimes.clear();
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.getServer() == null) return; // If client connected to a multiplayer server
            if (!client.getServer().isDedicated()) {
                if (client.player != null)
                    if (playerTimes.stream().noneMatch(playerTime -> playerTime.uuid.equals(client.player.getUuidAsString()))) {
                        playerTimes.add(new PlayerTimeMeter.PlayerTime(client.player, client.player.getUuidAsString(), System.currentTimeMillis()));
                        PlayerTimeMeter.getInstance().fireTimeChangedEvent(client.player);
                    }
            }
        });
    }
}
