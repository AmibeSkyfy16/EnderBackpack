package ch.skyfy.singlebackpack;

import ch.skyfy.singlebackpack.feature.PlayerTimeMeter;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static ch.skyfy.singlebackpack.SingleBackpack.DISABLED;
import static ch.skyfy.singlebackpack.SingleBackpack.MOD_CONFIG_DIR;

public class PlayTimeMeter {

    private static class PlayTimeMeterHolder {
        public static final PlayTimeMeter INSTANCE = new PlayTimeMeter();
    }

    public static PlayTimeMeter getInstance() {
        return PlayTimeMeter.PlayTimeMeterHolder.INSTANCE;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static PlayTimeMeter initialize() {
        return PlayTimeMeter.getInstance();
    }

    public AbstractMap.SimpleEntry<String, Long> time = null;

    private PlayerTimeMeter.TimeChangedEvent event;

    private final File playerTimeFile;

    private Long now;

    private Timer timeUpdater, timeSaver;

    private final List<PlayerTime> playerTimes;

    private PlayTimeMeter(){
        playerTimeFile = MOD_CONFIG_DIR.resolve("playerTime.dat").toFile();
        timeSaver = new Timer();
        playerTimes = new ArrayList<>();
        createConfigDir();

        // We save the player's time every 5 minutes, to be sure that the player's time is saved in case of a server crash
//        timeSaver.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                for (String uuid : SingleBackpack.playersConnected) {
//                    saveTimeForSpecificPlayer(uuid);
//                }
//            }
//        }, 300_000, 300_000);

        // When a specific time is reached, the size of the doc bag will increase
        timeUpdater = new Timer();
        timeUpdater.schedule(new TimerTask() {
            @Override
            public void run() {
                for (String uuid : SingleBackpack.playersConnected) {
//                    saveTimeForSpecificPlayer(uuid);
                    fireTimeChangedEvent(uuid);
                }
            }
        }, 1000, 1000);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (playerTimes.stream().noneMatch(playerTime -> playerTime.uuid.equals(handler.player.getUuidAsString()))) {
                playerTimes.add(new PlayerTime(handler.player.getUuidAsString(), System.currentTimeMillis()));
                var uuid = handler.player.getUuidAsString();
                fireTimeChangedEvent(getInstance().);
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            timeSaver.cancel();
            timeSaver.purge();
            timeUpdater.cancel();
            timeUpdater.purge();
            saveData(handler.getPlayer().getUuidAsString());



        });

    }

    private File createConfigDir(){
        var playerTimesFolder = SingleBackpack.MOD_CONFIG_DIR.resolve("playerTimes").toFile();
        if(!playerTimesFolder.exists())
            if(!playerTimesFolder.mkdir())DISABLED.set(true);
        return playerTimesFolder;
    }
    private void saveTimeForSpecificPlayer(String uuid){
        playerTimes.stream().filter(playerTime -> playerTime.uuid.equals(uuid)).findFirst().ifPresent(PlayerTime::saveTime);
    }
    public void registerTimeChangedEvent(PlayerTimeMeter.TimeChangedEvent event) {
        this.event = event;
    }

    public void fireTimeChangedEvent(Long time) {
        if (event != null) event.timeChanged(time);
    }


//    private void saveData(String uuid) {
//        var nbt = new NbtCompound();
//        var nbt2 = new NbtCompound();
//        nbt2.putLong("timePlayed", time.getValue());
//        nbt.put(uuid, nbt2);
//        try {
//            NbtIo.write(nbt, playerTimeFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private AbstractMap.SimpleEntry<String, Long> loadData(String uuid) {
//        if (!playerTimeFile.exists()) return new AbstractMap.SimpleEntry<>(uuid, 0L);
//        try {
//            var nbt = NbtIo.read(playerTimeFile);
//            if (nbt != null) {
//                var nbt2 = (NbtCompound) nbt.get(uuid);
//                if (nbt2 != null)
//                    return new AbstractMap.SimpleEntry<>(uuid, nbt2.getLong("timePlayed"));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new AbstractMap.SimpleEntry<>(uuid, 0L);
//    }

    private boolean isPlayerOnline(String uuid){
        return SingleBackpack.playersConnected.stream().anyMatch(playerConnectedUUID -> playerConnectedUUID.equals(uuid));
    }
    private static void get(File file) throws IOException {
        try (var reader = new FileReader(file)) {
            gson.fromJson(reader, (Type) Long.TYPE);
        }
    }
    static class PlayerTime {
        final String uuid;
        private Long startTime, time;
        private final File file;
        public PlayerTime(String uuid, Long startTime) {
            this.uuid = uuid;
            this.startTime = startTime;
            file = getInstance().playerTimesFolder.toPath().resolve(uuid + ".json").toFile();
            this.time = getTime();
        }

        private Long getTime(){
            if(file.exists()){
                try {
                    get(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return 0L;
        }

        public void saveTime() {
            if(!getInstance().isPlayerOnline(uuid))return;
            var elapsed = System.currentTimeMillis() - startTime;
            time += elapsed;
            startTime = System.currentTimeMillis();
            try {
                save(file, time);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface TimeChangedEvent {
        void timeChanged(Long time);
    }

}
