package ch.skyfy.singlebackpack;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BackpacksManager {

    public static final AtomicReference<Byte> CURRENT_ROWS = new AtomicReference<>(SingleBackpack.config.sizes.get(0L));

    public static void initialize() {
        // Determines when the player's backpack expands
        PlayTimeMeter.getInstance().registerTimeChangedEvent(time -> {
            var timeIsGreaterThan = new AtomicBoolean(true);
            var lastRow = new AtomicReference<>((byte) -1);
            var count = new AtomicInteger(0);
            SingleBackpack.config.sizes.forEach((keyTime, row) -> {
                if (!timeIsGreaterThan.get()) return;

                if(timeIsGreaterThan.get() && count.get() == SingleBackpack.config.sizes.size() - 1)
                    CURRENT_ROWS.set(row);

                if (time >= keyTime)
                    timeIsGreaterThan.set(true);
                 else {
                    if (timeIsGreaterThan.get())
                        CURRENT_ROWS.set(lastRow.get());
                    timeIsGreaterThan.set(false);
                }
                lastRow.set(row);
                count.getAndIncrement();
            });
        });
    }
}
