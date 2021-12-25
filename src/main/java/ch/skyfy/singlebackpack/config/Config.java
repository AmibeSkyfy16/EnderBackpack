package ch.skyfy.singlebackpack.config;

import java.util.LinkedHashMap;

public class Config {
    public Boolean givePlayerBackpack, disableCraft;
    public LinkedHashMap<Long, Byte> sizes;
    public Config(Boolean givePlayerBackpack, Boolean disableCraft, LinkedHashMap<Long, Byte> sizes) {
        this.givePlayerBackpack = givePlayerBackpack;
        this.disableCraft = disableCraft;
        this.sizes = sizes;
    }
}
