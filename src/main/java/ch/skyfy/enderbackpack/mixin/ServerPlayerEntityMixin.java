package ch.skyfy.enderbackpack.mixin;

import ch.skyfy.enderbackpack.BackpacksManager;
import ch.skyfy.enderbackpack.Configurator;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

import static ch.skyfy.enderbackpack.Configurator.MOD_CONFIG_DIR;

/**
 * If dropBackpackContentWhenDying is set to true in the config
 * we will drop backpack content if a player die
 */
@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @SuppressWarnings("ConstantConditions")
    @Inject(method = "onDeath", at = @At("TAIL"))
    public void onDeath(DamageSource damageSource, CallbackInfo info) {
        if (Configurator.getInstance().config.dropBackpackContentWhenDying) {
            var player = (((ServerPlayerEntity) (Object) this));
            var playerFile = MOD_CONFIG_DIR.resolve("backpacks").resolve(player.getUuidAsString() + ".dat").toFile();
            var size = BackpacksManager.playerRows.get(player.getUuidAsString()) * 9;
            try {
                var itemStacks = DefaultedList.ofSize(size, ItemStack.EMPTY);
                var nbtCompound = NbtIo.read(playerFile);
                if (nbtCompound == null) return;
                Inventories.readNbt(nbtCompound.getCompound("BackpackInventory"), itemStacks);
                for (var itemStack : itemStacks)
                    player.dropItem(itemStack, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            var nbtCompound = new NbtCompound();
            nbtCompound.put("BackpackInventory", Inventories.writeNbt(new NbtCompound(), DefaultedList.ofSize(size, ItemStack.EMPTY), true));
            try {
                NbtIo.write(nbtCompound, playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
