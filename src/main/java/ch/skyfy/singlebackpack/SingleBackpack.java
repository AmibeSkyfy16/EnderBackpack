package ch.skyfy.singlebackpack;

import ch.skyfy.singlebackpack.client.screen.BackpackScreenHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;

import java.nio.file.Path;

public class SingleBackpack implements ModInitializer {

    public static final String MODID = "single_backpack";

    public static Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("SingleBackpack");

    public static final GameRules.Key<GameRules.BooleanRule> GIVE_PLAYER_BACKPACK = GameRuleRegistry.register("givePlayerBackpack", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

    public static final Item BACKPACK = new BackpackItem(new Item.Settings().group(ItemGroup.MISC).maxCount(1));//creates your backpack

    public static final ScreenHandlerType<BackpackScreenHandler> BACKPACK_SCREEN_HANDLER;

    static {
        BACKPACK_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "backpack_screen"), BackpackScreenHandler::new); //registers your screen handler
    }

    @Override
    public void onInitialize() {
        createConfigDir();

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!world.getGameRules().getBoolean(GIVE_PLAYER_BACKPACK)) return;
            if (entity instanceof ServerPlayerEntity player) {
                var hasBackpack = false;
                for (var slot = 0; slot < player.getInventory().size(); slot++) {
                    System.out.println("getTranslationKey + " + player.getInventory().getStack(slot).getTranslationKey());
                    if (player.getInventory().getStack(slot).getTranslationKey().equalsIgnoreCase("item.single_backpack.backpack")) {
                        hasBackpack = true;
                    }
                }
                if (!hasBackpack) {
                    player.dropItem(new ItemStack(BACKPACK), false);
                    player.sendMessage(Text.of("Your backpack has been dropped, be sure to get it back"), false);
                }
            }
        });
        Registry.register(Registry.ITEM, new Identifier(MODID, "backpack"), BACKPACK);//registers your backpack
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createConfigDir() {
        var configDir = MOD_CONFIG_DIR.toFile();
        if (!configDir.exists()) configDir.mkdir();
        var backpacksFolderFile = MOD_CONFIG_DIR.resolve("backpacks").toFile();
        if (!backpacksFolderFile.exists()) backpacksFolderFile.mkdir();
    }

}
