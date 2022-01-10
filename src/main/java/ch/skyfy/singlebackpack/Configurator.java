package ch.skyfy.singlebackpack;


import ch.skyfy.singlebackpack.config.Config;
import ch.skyfy.singlebackpack.config.ConfigUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

import static ch.skyfy.singlebackpack.ConstantsMessage.CONFIG_FOLDER_COULD_NOT_BE_CREATED;

public final class Configurator {

    private final static class ConfiguratorHolder {
        private static final Configurator INSTANCE = new Configurator();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Configurator getInstance() {
        return ConfiguratorHolder.INSTANCE;
    }

    public static Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("SingleBackpack");

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
     *
     * SingleBackpack
     *      this folder is the root folder located in config folder of server
     *
     * backpacks folder
     *      this folder is located in SingleBackpack folder and will be used for save the inventories of player
     *
     * backup folder
     *      this folder is located in backpacks folder and will be used to make backup of player inventory to prevent any lost inventories
     *
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

        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            if (!backpacksFolderFile.exists())
                backpacksFolderFileCreated = backpacksFolderFile.mkdir();

            if (!backpacksBackupFolderFile.exists())
                backpacksBackupFolderFileCreated = backpacksBackupFolderFile.mkdir();
        }

        if (!configDirCreated || !backpacksFolderFileCreated || !backpacksBackupFolderFileCreated)
            Deactivator.getInstance().disable(CONFIG_FOLDER_COULD_NOT_BE_CREATED.getMessage());
    }

}
