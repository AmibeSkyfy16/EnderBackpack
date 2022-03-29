package ch.skyfy.enderbackpack;


import ch.skyfy.enderbackpack.config.Config;
import ch.skyfy.enderbackpack.config.ConfigUtils;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

import static ch.skyfy.enderbackpack.ConstantsMessage.CONFIG_FOLDER_COULD_NOT_BE_CREATED;

public final class Configurator {

    private final static class ConfiguratorHolder {
        private static final Configurator INSTANCE = new Configurator();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Configurator getInstance() {
        return ConfiguratorHolder.INSTANCE;
    }

    public static Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("EnderBackpack");

    /**
     * Try to initialize the config
     * if an exception occurs, return true and mod will be disabled
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean initialize() {
        getInstance();
        return Deactivator.getInstance().isDisabled();
    }

    public final Config config;

    private Configurator() {
        createConfigDirectories();
        config = ConfigUtils.getOrCreateConfig(Config.class, "config.json");
    }


    /**
     * Create configuration folders essential for the mod
     * <p>
     * SingleBackpack
     * this folder is the root folder located in config folder of server
     * <p>
     * backpacks folder
     * this folder is located in EnderBackpack folder and will be used for save the inventories of player
     * <p>
     * backup folder
     * this folder is located in backpacks folder and will be used to make backup of player inventory to prevent any lost inventories
     * <p>
     * if something wrong happen, like a folder cannot be created, the mod will be disabled
     */
    private void createConfigDirectories() {
        var configDir = MOD_CONFIG_DIR.toFile();
        var backpacksFolderFile = MOD_CONFIG_DIR.resolve("backpacks").toFile();
        var backpacksBackupFolderFile = backpacksFolderFile.toPath().resolve("backup").toFile();

        var configDirCreated = true;
        var backpacksFolderFileCreated = true;
        var backpacksBackupFolderFileCreated = true;

        if (!configDir.exists())
            configDirCreated = configDir.mkdir();

        if (!backpacksFolderFile.exists())
            backpacksFolderFileCreated = backpacksFolderFile.mkdir();
        if (!backpacksBackupFolderFile.exists())
            backpacksBackupFolderFileCreated = backpacksBackupFolderFile.mkdir();


        if (!configDirCreated || !backpacksFolderFileCreated || !backpacksBackupFolderFileCreated)
            Deactivator.getInstance().disable(CONFIG_FOLDER_COULD_NOT_BE_CREATED.getMessage());
    }

}
