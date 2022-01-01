package ch.skyfy.singlebackpack;

import ch.skyfy.singlebackpack.client.screen.BackpackScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BackpackItem extends Item {

    public BackpackItem(Settings settings) {
        super(settings);
    }

    //called whenever you right-click your backpack
    //we are going to request a screen if the player is not sneaking
    //you can change the behaviour if you like
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (!user.isSneaking()) {
                user.openHandledScreen(new NamedScreenHandlerFactory() {
                    @Override
                    public Text getDisplayName() {
                        return new TranslatableText("item.single_backpack.backpack");//lang/en_us.json
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                        return new BackpackScreenHandler(syncId, inv, new BackpackInventory(BackpacksManager.playerRows.get(player.getUuidAsString()) * 9, user.getStackInHand(hand), user.getUuidAsString()));
                    }
                });
            }
        }
        return super.use(world, user, hand);
    }
}
