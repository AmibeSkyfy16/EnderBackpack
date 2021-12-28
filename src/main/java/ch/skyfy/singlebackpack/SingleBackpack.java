package ch.skyfy.singlebackpack;

import ch.skyfy.singlebackpack.client.screen.BackpackScreenHandler;
import ch.skyfy.singlebackpack.config.Config;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SingleBackpack implements ModInitializer {

    public static final String MODID = "single_backpack";

    public static Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("SingleBackpack");

    public static Config config;

    public static final Item BACKPACK = new BackpackItem(new Item.Settings().group(ItemGroup.MISC).maxCount(1));//creates your backpack

    public static final ScreenHandlerType<BackpackScreenHandler> BACKPACK_SCREEN_HANDLER;

    static {
        BACKPACK_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "backpack_screen"), BackpackScreenHandler::new); //registers your screen handler
    }

    @Override
    public void onInitialize() {
        config = createConfig();
        BackpacksManager.initialize();
        registerEvents();
        registerItem();
    }

    private void registerItem() {
        Registry.register(Registry.ITEM, new Identifier(MODID, "backpack"), BACKPACK);//registers your backpack
    }

    private void registerEvents() {
        ServerEntityEvents.ENTITY_LOAD.register(this::givePlayerBackpack);
    }

    private void givePlayerBackpack(Entity entity, ServerWorld world) {
        if (!config.givePlayerBackpack) return;
        if (entity instanceof ServerPlayerEntity player) {
            var hasBackpack = false;
            for (var slot = 0; slot < player.getInventory().size(); slot++)
                if (player.getInventory().getStack(slot).getTranslationKey().equalsIgnoreCase("item.single_backpack.backpack"))
                    hasBackpack = true;
            if (!hasBackpack) {
                player.dropItem(new ItemStack(BACKPACK), false);
                player.sendMessage(Text.of("Your backpack has been dropped, be sure to get it back"), false);
            }
        }
    }

    /**
     * Create configuration file and folder essential to the mod
     * if something wrong happen, like a folder or a file cannot be created, the mod will be disabled
     *
     * @return Config or null
     */
    @SuppressWarnings("CommentedOutCode")
    private @Nullable Config createConfig() {
        var configDir = MOD_CONFIG_DIR.toFile();
        var backpacksFolderFile = MOD_CONFIG_DIR.resolve("backpacks").toFile();
        if (!configDir.exists())
            if (!configDir.mkdir()) return null;

        if (!backpacksFolderFile.exists())
            if (!backpacksFolderFile.mkdir()) return null;

        var gson = new GsonBuilder().setPrettyPrinting().create();
        var configFile = MOD_CONFIG_DIR.resolve("config.json").toFile();
        Config config = null;
        try {
            if (!configFile.exists()) {
                var sizes = new LinkedHashMap<Long, Byte>();
//                sizes.put(0L, (byte) 1);
//                sizes.put(10_000L, (byte) 2);
//                sizes.put(20_000L, (byte) 3);
//                sizes.put(30_000L, (byte) 4);
//                sizes.put(40_000L, (byte) 5);
//                sizes.put(50_000L, (byte) 6);

                sizes.put(0L, (byte)1);
                sizes.put(10800000L, (byte)2); // 3 hours
                sizes.put(21600000L, (byte)3); // 6 hours
                sizes.put(86400000L, (byte)4); // 24 hours
                sizes.put(172800000L, (byte)5); // 48 hours
                sizes.put(259200000L, (byte)6); // 72 hours

                config = new Config(false, false, sizes);
                var writer = new FileWriter(configFile);
                gson.toJson(config, writer);
                writer.flush();
                writer.close();
            } else {
                var reader = new FileReader(configFile);
                config = gson.fromJson(reader, Config.class);
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    public static JsonElement createBackpackRecipe() {
        return createShapedRecipeJson(
                Lists.newArrayList('L', 'P', 'S', 'C', 'W'),
                Lists.newArrayList(new Identifier("leather"), new Identifier("lead"), new Identifier("string"), new Identifier("chest"), new Identifier("wool")),
                Lists.newArrayList("item", "item", "item", "item", "tag"),
                Lists.newArrayList(
                        "LPL",
                        "SCS",
                        "WWW"
                ),
                new Identifier("single_backpack:backpack")
        );
    }

    // see https://fabricmc.net/wiki/tutorial:dynamic_recipe_generation
    private static JsonObject createShapedRecipeJson(ArrayList<Character> keys, ArrayList<Identifier> items, ArrayList<String> type, ArrayList<String> pattern, Identifier output) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(pattern.get(0));
        jsonArray.add(pattern.get(1));
        jsonArray.add(pattern.get(2));
        json.add("pattern", jsonArray);

        JsonObject individualKey;
        JsonObject keyList = new JsonObject();

        for (int i = 0; i < keys.size(); ++i) {
            individualKey = new JsonObject();
            items.forEach(System.out::println);
            type.forEach(System.out::println);
            System.out.println("I " + i);

            individualKey.addProperty(type.get(i), items.get(i).toString()); //This will create a key in the form "type": "input", where type is either "item" or "tag", and input is our input item.
            keyList.add(keys.get(i) + "", individualKey); //Then we add this key to the main key object.
        }

        json.add("key", keyList);

        JsonObject result = new JsonObject();
        result.addProperty("item", output.toString());
        result.addProperty("count", 1);
        json.add("result", result);

        return json;
    }

}
