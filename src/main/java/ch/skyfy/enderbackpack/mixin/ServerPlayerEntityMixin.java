package ch.skyfy.enderbackpack.mixin;

import ch.skyfy.enderbackpack.Configurator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

import static ch.skyfy.enderbackpack.Configurator.MOD_CONFIG_DIR;

// TODO drop items on the ground when dying
@SuppressWarnings("ALL")
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void onDeath(DamageSource damageSource, CallbackInfo info) {
        if(0 == 0)return;
        if (Configurator.getInstance().config.dropBackpackContentWhenDying) {
            System.out.println("d: " + damageSource.getName() + damageSource.getSource().getClass().getName());
            if (damageSource.getSource() instanceof PlayerEntity player) {
                var playerUUID = player.getUuidAsString();
                var playerFile = MOD_CONFIG_DIR.resolve("backpacks").resolve(playerUUID + ".dat").toFile();
                System.out.println("playerFile: " + playerFile.toString());
                try {
                    var data = DefaultedList.<ItemStack>of();
                    var nbtCompound = NbtIo.read(playerFile);
                    if (nbtCompound == null) return;
                    Inventories.readNbt(nbtCompound.getCompound("BackpackInventory"), data);

                    for (ItemStack datum : data) {
                        System.out.println("Item: " + datum.getTranslationKey());
//                        player.dropItem(datum, true);
//                        player.dropItem(datum, true, false);
                    }

//                    nbtCompound.put("BackpackInventory", Inventories.writeNbt(new NbtCompound(), list, true));

//                    ItemStack.EMPTY.getOrCreateNbt().put("BackpackInventory", Inventories.writeNbt(new NbtCompound(), DefaultedList.ofSize(0, ItemStack.EMPTY), true));
//                    Inventories.readNbt(nbtCompound.getCompound("BackpackInventory"), this.list);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playerFile.delete();
            } else {
                System.out.println("SOURCE");
            }
        }
        System.out.println("Player dead");
    }

}
