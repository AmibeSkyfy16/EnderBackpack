package ch.skyfy.singlebackpack.config;

import java.util.LinkedHashMap;

public class Config {
    private static final boolean defaultGivePlayerBackpack = false;
    private static final boolean defaultDisableCraft = false;
    private static final LinkedHashMap<Long, Byte> defaultSizes = new LinkedHashMap<>() {{
        put(0L, (byte) 1);
        put(3_600_000L, (byte) 2); // 1 hours
        put(7_200_000L, (byte) 3); // 2 hours
        put(14_400_000L, (byte) 4); // 4 hours
        put(43_200_000L, (byte) 5); // 12 hours
        put(129_600_000L, (byte) 6); // 36 hours
    }};
    public Boolean givePlayerBackpack, disableCraft;
    public LinkedHashMap<Long, Byte> sizes;
    public Config() {
        this.givePlayerBackpack = defaultGivePlayerBackpack;
        this.disableCraft = defaultDisableCraft;
        this.sizes = defaultSizes;
    }
}
