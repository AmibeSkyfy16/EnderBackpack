package ch.skyfy.enderbackpack;

import ch.skyfy.enderbackpack.feature.PlayerTimeMeter;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static ch.skyfy.enderbackpack.Configurator.MOD_CONFIG_DIR;


public class BackpacksManager {

    /**
     * Allows knowing what is the current size of the backpack of each player
     */
    public static final Map<String, Integer> playerRows = new HashMap<>();

    /**
     * contains a key that is an uuid and a DataChecker object
     * It is shared between client and server for being sure client and server use same "row" to create the correct size of the inventory
     */
    public static final Map<String, DataChecker> verificator = new HashMap<>();

    /**
     * This class is used to be sure ROW (use to calculate the size of the backpack) are equal on both (client and server) in a specific time
     */
    public static class DataChecker {
        public Long date;
        public int row;
        public AtomicBoolean clientRespond;

        public DataChecker(int row, AtomicBoolean clientRespond) {
            this.date = System.currentTimeMillis();
            this.row = row;
            this.clientRespond = clientRespond;
        }
    }

    /**
     * this code is called every 2 seconds
     * <p>
     * Determines whether the player's backpack should grow according to the amount of playing time the player has with the server
     * <p>
     * remember: The playing time of a player on the server is updated every 2 seconds and is saved on the disk every 12 minutes. The time is also saved to disk if the player leaves the server
     *
     * @param player the player
     * @param time   the new total time played for the player on this server
     */
    public static void growBackpack(PlayerEntity player, Long time) {
        var targetRow = -1;
        for (Map.Entry<Long, Integer> entry : Configurator.getInstance().config.sizes.entrySet()) {
            if (time < entry.getKey()) break;
            targetRow = entry.getValue();
        }
        if (targetRow != -1) {
            playerRows.put(player.getUuidAsString(), targetRow);
            if (player instanceof ServerPlayerEntity serverPlayerEntity) {
                sendDataToPlayer(serverPlayerEntity, targetRow);
            }
        }
    }

    /**
     * This code is executed server side !
     * Server will send to client the correct row to use when client has to render the backpack inventory for a player
     *
     * @param serverPlayerEntity player
     * @param row                row
     */
    public static void sendDataToPlayer(ServerPlayerEntity serverPlayerEntity, int row) {
        var data = new DataChecker(row, new AtomicBoolean(false));
        verificator.put(serverPlayerEntity.getUuidAsString(), data);
        ServerPlayNetworking.send(serverPlayerEntity, new Identifier("rowsent"), PacketByteBufs.create().writeNbt(new NbtCompound() {{
            putInt(serverPlayerEntity.getUuidAsString(), row);
            putLong("date", data.date);
        }}));
    }

    public static void initialize() {
        PlayerTimeMeter.getInstance().registerTimeChangedEvent(BackpacksManager::growBackpack);
        backupTimer();
        registerCommunication();
    }

    @SuppressWarnings("ConstantConditions")
    private static void registerCommunication() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(new Identifier("rowsent"), (client, handler, buf, responseSender) -> {
//                System.out.println("ClientPlayNetworking.registerGlobalReceiver(new Identifier(\"rowsent\")");
                var nbt = buf.readNbt();
                client.execute(() -> {
                    var row = nbt.getInt(client.player.getUuidAsString());
                    var date = nbt.getLong("date");
                    playerRows.put(client.player.getUuidAsString(), row);

                    // Client will reply to server that he received the row,
                    var replyNbt = new NbtCompound();
                    replyNbt.putInt(client.player.getUuidAsString(), row);
                    replyNbt.putLong("date", date);
                    ClientPlayNetworking.send(new Identifier("rowreceived"), PacketByteBufs.create().writeNbt(replyNbt));
                });
            });
        } else {
            ServerPlayNetworking.registerGlobalReceiver(new Identifier("rowreceived"), (server, player, handler, buf, responseSender) -> {
//                System.out.println("ServerPlayNetworking.registerGlobalReceiver(new Identifier(\"rowreceived\")");
                var nbt = (NbtCompound) buf.readNbt();
                server.execute(() -> {
                    var row = nbt.getInt(player.getUuidAsString());
                    var date = nbt.getLong("date");
                    var data = verificator.get(player.getUuidAsString());
                    if (data.date == date && data.row == row) {
                        if (!data.clientRespond.get()) data.clientRespond.set(true);
                    }
                });
            });
        }
    }

    private static void backupTimer() {
        // For prevents some lost data, we do a backup of all player inventory .dat file
        new Timer(true).schedule(new TimerTask() {
            private final File backpacksFolder = MOD_CONFIG_DIR.resolve("backpacks").toFile();
            private final File backpacksBackupFolderFile = backpacksFolder.toPath().resolve("backup").toFile();

            @Override
            public void run() {
                var files = backpacksFolder.listFiles();
                if (files == null) return;
                for (var file : files) {
                    if (file.isDirectory()) continue; // We don't have to copy backup folder
                    var now = LocalDateTime.now();
                    var dateFolderName = String.format("%d-%02d-%02d@%02d-%02d-%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
                    var dateFolderFile = backpacksBackupFolderFile.toPath().resolve(dateFolderName).toFile();
                    if (!dateFolderFile.exists())
                        if (!dateFolderFile.mkdir()) return;
                    try {
                        FileUtils.copyFileToDirectory(file, dateFolderFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 10_000, 7_200_000L);
    }
}
