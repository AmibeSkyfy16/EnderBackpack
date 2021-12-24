package ch.skyfy.singlebackpack.config;

import ch.skyfy.singlebackpack.Size;

import java.util.HashMap;

public class Config {
    public Boolean givePlayerBackpack, disableCraft;
    public HashMap<Long, Size> sizes;
    public Config(Boolean givePlayerBackpack, Boolean disableCraft, HashMap<Long, Size> sizes) {
        this.givePlayerBackpack = givePlayerBackpack;
        this.disableCraft = disableCraft;
        this.sizes = sizes;
    }
}
