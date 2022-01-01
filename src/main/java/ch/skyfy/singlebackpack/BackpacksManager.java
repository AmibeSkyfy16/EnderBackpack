package ch.skyfy.singlebackpack;

import ch.skyfy.singlebackpack.feature.PlayerTimeMeter;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BackpacksManager {

    public static final Map<String, Byte> playerRows = new HashMap<>();

    public static void getCorrectInventorySize(String uuid, Long time) {
        var timeIsGreaterThan = new AtomicBoolean(true);
        var lastRow = new AtomicReference<>((byte) -1);
        var count = new AtomicInteger(0);
        SingleBackpack.config.sizes.forEach((keyTime, row) -> {
            if (!timeIsGreaterThan.get()) return;
            if (timeIsGreaterThan.get() && count.get() == SingleBackpack.config.sizes.size() - 1) {
                playerRows.put(uuid, row);
                return;
            }
            if (time >= keyTime)
                timeIsGreaterThan.set(true);
            else {
                if (timeIsGreaterThan.get())
                    playerRows.put(uuid, lastRow.get());
                timeIsGreaterThan.set(false);
            }
            lastRow.set(row);
            count.getAndIncrement();
        });
    }

    @SuppressWarnings("ConstantConditions")
    public static void initialize() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(new Identifier("test"), (client, handler, buf, responseSender) -> {
                var nbt = buf.readNbt();
                var uuid = nbt.getKeys().toArray(String[]::new)[0];
                var time = nbt.getLong(uuid);
                System.out.println("Row received from server : " + playerRows.get(uuid));
                client.execute(() -> getCorrectInventorySize(uuid, time));
                System.out.println("2. Row received from server : " + playerRows.get(uuid));
            });
        }
        // Determines when the player's backpack expands
        PlayerTimeMeter.getInstance().registerTimeChangedEvent(BackpacksManager::getCorrectInventorySize);
    }
}
