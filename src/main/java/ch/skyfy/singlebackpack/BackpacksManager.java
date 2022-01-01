package ch.skyfy.singlebackpack;

import ch.skyfy.singlebackpack.feature.PlayerTimeMeter;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BackpacksManager {

    public static final Map<String, Byte> playerRows = new HashMap<>();

//    public static final List<DataChecker> dataCheckers = new ArrayList<>();

    public static final Map<String, DataChecker> verificator = new HashMap<>();

    /**
     * This class is used to be sure ROW are equal on both (client and server)
     */
    public static class DataChecker {
        public Long date;
        public Byte row;
        public AtomicBoolean clientRespond;

        public DataChecker(Byte row, AtomicBoolean clientRespond) {
            this.date = System.currentTimeMillis();
            this.row = row;
            this.clientRespond = clientRespond;
        }
    }

    /**
     * This code is called Server Side !!!
     * This code is code every time a player time is calculated
     *      see PlayerTimeMeter.getInstance().registerTimeChangedEvent(BackpacksManager::getCorrectInventorySize);
     * @param player the player
     * @param time the new total time for the player on this server
     */
    public static void getCorrectInventorySize(ServerPlayerEntity player, Long time) {
        var timeIsGreaterThan = new AtomicBoolean(true);
        var lastRow = new AtomicReference<>((byte) -1);
        var count = new AtomicInteger(0);
        SingleBackpack.config.sizes.forEach((keyTime, row) -> {
            if (!timeIsGreaterThan.get()) return;
            if (timeIsGreaterThan.get() && count.get() == SingleBackpack.config.sizes.size() - 1) {
                playerRows.put(player.getUuidAsString(), row);
                sendDataToPlayer(player, row);
                return;
            }
            if (time >= keyTime)
                timeIsGreaterThan.set(true);
            else {
                if (timeIsGreaterThan.get()) {
                    playerRows.put(player.getUuidAsString(), lastRow.get());
                    sendDataToPlayer(player, lastRow.get());
                }
                timeIsGreaterThan.set(false);
            }
            lastRow.set(row);
            count.getAndIncrement();
        });
    }

    public static void sendDataToPlayer(ServerPlayerEntity serverPlayerEntity, Byte row) {
        var data = new DataChecker(row, new AtomicBoolean(false));
        verificator.put(serverPlayerEntity.getUuidAsString(), data);

        var nbt = new NbtCompound();
        nbt.putByte(serverPlayerEntity.getUuidAsString(), row);
        nbt.putLong("date", data.date);
        ServerPlayNetworking.send(serverPlayerEntity, new Identifier("sendrow"), PacketByteBufs.create().writeNbt(nbt));
    }

    @SuppressWarnings("ConstantConditions")
    public static void initialize() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(new Identifier("sendrow"), (client, handler, buf, responseSender) -> {
                var nbt = buf.readNbt();
                client.execute(() -> {
                    var row = nbt.getByte(client.player.getUuidAsString());
                    var date = nbt.getLong("date");
                    playerRows.put(client.player.getUuidAsString(), row);
                    System.out.println(System.currentTimeMillis() + " ROW ADDED : " + row);

                    // Client will reply to server that he received the row,
                    var replyNbt = new NbtCompound();
                    replyNbt.putByte(client.player.getUuidAsString(), row);
                    replyNbt.putLong("date", date);
                    ClientPlayNetworking.send(new Identifier("rowreceived"), PacketByteBufs.create().writeNbt(replyNbt));
                });
            });
        }else {

            ServerPlayNetworking.registerGlobalReceiver(new Identifier("rowreceived"), (server, player, handler, buf, responseSender) -> {
                var nbt = (NbtCompound)buf.readNbt();
                server.execute(() -> {
                    var row = nbt.getByte(player.getUuidAsString());
                    var date = nbt.getLong("date");
                    var data = verificator.get(player.getUuidAsString());
                    if(data.date == date && data.row == row){
                        if(!data.clientRespond.get())data.clientRespond.set(true);
                    }
                });
            });

            // This code is called server side
            // Determines when the player's backpack expands
            PlayerTimeMeter.getInstance().registerTimeChangedEvent(BackpacksManager::getCorrectInventorySize);
        }
    }
}
