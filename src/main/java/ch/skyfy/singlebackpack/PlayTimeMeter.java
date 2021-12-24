package ch.skyfy.singlebackpack;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Timer;
import java.util.TimerTask;

import static ch.skyfy.singlebackpack.SingleBackpack.MOD_CONFIG_DIR;

public class PlayTimeMeter {

    private static class PlayTimeMeterHolder {
        public static final PlayTimeMeter INSTANCE = new PlayTimeMeter();
    }

    public static PlayTimeMeter getInstance() {
        return PlayTimeMeter.PlayTimeMeterHolder.INSTANCE;
    }

    public AbstractMap.SimpleEntry<String, Long> time = null;

    private TimeChangedEvent event;

    private final File playerTimeFile;

    private Long now;

    private Timer timeUpdater, timeSaver;

    {
        playerTimeFile = MOD_CONFIG_DIR.resolve("playerTime.dat").toFile();
        initialize();
    }

    public void initialize() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null)
                time = loadData(client.player.getUuidAsString());

            now = System.currentTimeMillis();

            // We save the player's time every 5 minutes, to be sure that the player's time is saved in case of a crash
            timeSaver = new Timer();
            timeSaver.schedule(new TimerTask() {
                @Override
                public void run() {
                    time.setValue(time.getValue() + (System.currentTimeMillis() - now));
                    now = System.currentTimeMillis();
                    saveData();
                }
            }, 300_000, 300_000);

            // When a specific time is reached, the size of the doc bag will increase
            timeUpdater = new Timer();
            timeUpdater.schedule(new TimerTask() {
                @Override
                public void run() {
                    time.setValue(time.getValue() + (System.currentTimeMillis() - now));
                    now = System.currentTimeMillis();
                    fireTimeChangedEvent();
                }
            }, 1000, 1000);
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            timeSaver.cancel();
            timeSaver.purge();
            timeUpdater.cancel();
            timeUpdater.purge();
            saveData();
        });
    }

    public void registerTimeChangedEvent(TimeChangedEvent event) {
        this.event = event;
    }

    public void fireTimeChangedEvent() {
        if (event != null) event.timeChanged(this.time.getValue());
    }

    private void saveData() {
        var nbt = new NbtCompound();
        nbt.putLong("timePlayed", time.getValue());
        try {
            NbtIo.write(nbt, playerTimeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AbstractMap.SimpleEntry<String, Long> loadData(String uuid) {
        if (!playerTimeFile.exists()) return new AbstractMap.SimpleEntry<>(uuid, 0L);
        try {
            var nbt = NbtIo.read(playerTimeFile);
            if (nbt != null) {
                return new AbstractMap.SimpleEntry<>(uuid, nbt.getLong("timePlayed"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new AbstractMap.SimpleEntry<>(uuid, 0L);
    }

    public interface TimeChangedEvent {
        void timeChanged(Long time);
    }

}
