package ch.skyfy.enderbackpack;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * If something happens that we do not want to happen.
 * The mod will be deactivated and a message will be displayed in the console to warn the user
 * <p>
 * Example, If the values in a .json configuration file do not match the program expectation or the .json file is not valid
 */
public class Deactivator extends AtomicBoolean {

    static class DeactivatorHolder {
        private static final Deactivator INSTANCE = new Deactivator();
    }

    public static Deactivator getInstance() {
        return DeactivatorHolder.INSTANCE;
    }

    private static final String DISABLED_MESSAGE = "Mod has been disabled due to error";

    private final AtomicBoolean DISABLE;

    private Deactivator() {
        DISABLE = new AtomicBoolean();
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean disable(String message) {
        if (DISABLE.compareAndSet(false, isDisabled())) {
            if(message.isEmpty()) EnderBackpack.LOGGER.fatal(message);
            EnderBackpack.LOGGER.fatal(DISABLED_MESSAGE);
        }
        return isDisabled();
    }

    public boolean isDisabled() {
        return DISABLE.get();
    }

}
