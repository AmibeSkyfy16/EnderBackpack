package ch.skyfy.enderbackpack;

import ch.skyfy.enderbackpack.client.screen.BackpackScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BackpackItem extends Item {

    public BackpackItem(Settings settings) {
        super(settings);
    }

    // called whenever you right-click your backpack
    // we are going to request a screen if the player is not sneaking
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (!user.isSneaking()) {
                // We want to be sure client and server have correct row
                // If server change row, ask client to change row also, but client not reply, we will skip the code

                // TODO i think we dont need this anymore
//                if (!BackpacksManager.verificator.get(user.getUuidAsString()).clientRespond.get())
//                    return TypedActionResult.consume(user.getStackInHand(hand));

//                EnderBackpack.LOGGER.info("[BackpackItem.class] -> side: " + FabricLoader.getInstance().getEnvironmentType().name());
                user.openHandledScreen(createScreenHandler(user, user.getStackInHand(hand)));
            }
        }
        return super.use(world, user, hand);
    }

    public static ExtendedScreenHandlerFactory createScreenHandler(PlayerEntity user, ItemStack stack) {
//        System.out.println("side : " + FabricLoader.getInstance().getEnvironmentType().name());

        var buf = PacketByteBufs.create().writeVarInt(BackpacksManager.playerRows.get(user.getUuidAsString())).writeItemStack(stack);

        return new ExtendedScreenHandlerFactory() {

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                return new BackpackScreenHandler(syncId, inv, buf);
            }

            @Override
            public Text getDisplayName() {
                return Text.translatable("item.ender_backpack.backpack");
            }

            @Override
            public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                buf.writeVarInt(BackpacksManager.playerRows.get(user.getUuidAsString())).writeItemStack(stack);
            }
        };
    }
}
