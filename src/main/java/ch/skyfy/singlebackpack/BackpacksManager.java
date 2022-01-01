package ch.skyfy.singlebackpack;

import ch.skyfy.singlebackpack.feature.PlayerTimeMeter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BackpacksManager {

//    public static final AtomicReference<Byte> CURRENT_ROWS = new AtomicReference<>(SingleBackpack.config.sizes.get(0L));

    public static final Map<String, Byte> playerRows = new HashMap<>();

    public static void initialize() {
        // Determines when the player's backpack expands
        PlayerTimeMeter.getInstance().registerTimeChangedEvent((uuid, time) -> {
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
        });
    }
}
