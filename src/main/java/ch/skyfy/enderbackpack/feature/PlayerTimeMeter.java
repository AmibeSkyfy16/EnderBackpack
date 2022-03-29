package ch.skyfy.enderbackpack.feature;


import ch.skyfy.enderbackpack.Deactivator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static ch.skyfy.enderbackpack.Configurator.MOD_CONFIG_DIR;
import static ch.skyfy.enderbackpack.ConstantsMessage.CONFIG_FOLDER_COULD_NOT_BE_CREATED;

/**
 * The purpose of this class is to record the player's time.
 * <p>
 * PlayerTimeMeter will use a Timer to calculate an elapsed time and keep total player time up to date
 * The Timer is called every 2 secondes, but only every 6 minutes, a backup of player total time will be saved to a .json file
 * <p>
 * PlayerTimeMeter has also an inner interface called TimeChangedEvent
 * Class BackpackManager will register this interface for being notified when the player time changed
 */
public class PlayerTimeMeter {

    private static class PlayerTimeMeterHolder {
        public static final PlayerTimeMeter INSTANCE = new PlayerTimeMeter();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static PlayerTimeMeter getInstance() {
        return PlayerTimeMeterHolder.INSTANCE;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initialize() {
        getInstance();
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public final List<PlayerTime> playerTimes;

    private final File playerTimesFolder;

    private TimeChangedEvent event;

    private Timer saverTimer;

    public PlayerTimeMeter() {
        playerTimesFolder = createConfigDir();
        playerTimes = new ArrayList<>();
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            registerEvents();
            startSaverTimer();
        }
    }

    private File createConfigDir() {
        var playerTimesFolder = MOD_CONFIG_DIR.resolve("playerTimes").toFile();
        if (!playerTimesFolder.exists())
            if (!playerTimesFolder.mkdir())
                Deactivator.getInstance().disable(CONFIG_FOLDER_COULD_NOT_BE_CREATED.getMessage());
        return playerTimesFolder;
    }

    private void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (playerTimes.stream().noneMatch(playerTime -> playerTime.uuid.equals(handler.player.getUuidAsString()))) {
                playerTimes.add(new PlayerTime(handler.player, handler.player.getUuidAsString(), System.currentTimeMillis()));
                fireTimeChangedEvent(handler.player);
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            playerTimes.forEach(playerTime -> {
                if (playerTime.uuid.equals(handler.player.getUuidAsString()))
                    playerTime.saveTimeToDisk();
            });
            playerTimes.removeIf(playerTime -> playerTime.uuid.equals(handler.player.getUuidAsString()));
        });
    }

    public void stopSaverTimer(){
        if(saverTimer != null){
            saverTimer.cancel();
            saverTimer.purge();
        }
    }

    public void startSaverTimer() {
        saverTimer = new Timer(true);
        saverTimer.schedule(new TimerTask() {
            private int count = 0;

            @Override
            public void run() {
                for (var playerTime : playerTimes) {
                    playerTime.saveTime();
                    fireTimeChangedEvent(playerTime.player);
                }
                if (count >= 360) {
                    playerTimes.forEach(PlayerTime::saveTimeToDisk);
                    count = 0;
                }
                count++;
            }
        }, 2000, 2000);
    }

    public void registerTimeChangedEvent(TimeChangedEvent event) {
        this.event = event;
    }

    public void fireTimeChangedEvent(PlayerEntity player) {
        if (event != null) event.timeChanged(player, getTime(player.getUuidAsString()));
    }

    private void saveTimeForSpecificPlayer(String uuid) {
        playerTimes.stream().filter(playerTime -> playerTime.uuid.equals(uuid)).findFirst().ifPresent(PlayerTime::saveTime);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public Long getTime(String uuid) {
        saveTimeForSpecificPlayer(uuid);
        return playerTimes.stream().filter(playerTime -> playerTime.uuid.equals(uuid)).findFirst().get().time;
    }

    private static void save(File file, Long time) throws IOException {
        try (var writer = new FileWriter(file)) {
            gson.toJson(time, Long.TYPE, writer);
        }
    }

    private static Long get(File file) throws IOException {
        try (var reader = new FileReader(file)) {
            return gson.fromJson(reader, (Type) Long.TYPE);
        }
    }

    public static class PlayerTime {
        private final PlayerEntity player;
        public final String uuid;
        private Long startTime, time;
        private final File file;

        public PlayerTime(PlayerEntity player, String uuid, Long startTime) {
            this.player = player;
            this.uuid = uuid;
            this.startTime = startTime;
            file = getInstance().playerTimesFolder.toPath().resolve(uuid + ".json").toFile();
            this.time = getTime();
        }

        private Long getTime() {
            if (file.exists()) {
                try {
                    return get(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return 0L;
        }

        public Long saveTime() {
            var elapsed = System.currentTimeMillis() - startTime;
            time += elapsed;
            startTime = System.currentTimeMillis();
            return time;
        }

        public void saveTimeToDisk() {
            try {
                save(file, saveTime());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface TimeChangedEvent {
        void timeChanged(PlayerEntity player, Long time);
    }

}
