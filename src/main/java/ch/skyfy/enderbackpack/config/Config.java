package ch.skyfy.enderbackpack.config;

import java.util.LinkedHashMap;

public class Config {

    private static final boolean defaultGivePlayerBackpack = false;
    private static final boolean defaultDisableCraft = false;
    private static final boolean defaultDropBackpackContentWhenDying = true;
    private static final LinkedHashMap<Long, Integer> defaultSizes = new LinkedHashMap<>() {{
        put(0L, 1);
        put(3_600_000L, 2); // 1 hours
        put(7_200_000L, 3); // 2 hours
        put(14_400_000L, 4); // 4 hours
        put(43_200_000L, 5); // 12 hours
        put(129_600_000L, 6); // 36 hours
    }};

    public Boolean givePlayerBackpack, disableCraft, dropBackpackContentWhenDying;
    public LinkedHashMap<Long, Integer> sizes;

    public Config() {
        this.givePlayerBackpack = defaultGivePlayerBackpack;
        this.disableCraft = defaultDisableCraft;
        this.dropBackpackContentWhenDying = defaultDropBackpackContentWhenDying;
        this.sizes = defaultSizes;
    }
}
