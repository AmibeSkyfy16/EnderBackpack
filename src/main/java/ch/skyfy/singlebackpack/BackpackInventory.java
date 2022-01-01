package ch.skyfy.singlebackpack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.collection.DefaultedList;

import java.io.File;
import java.io.IOException;

import static ch.skyfy.singlebackpack.SingleBackpack.MOD_CONFIG_DIR;

public class BackpackInventory implements Inventory {

    private final DefaultedList<ItemStack> list;

    private final int size;

    private final ItemStack container;

    private final File playerFile;

    public BackpackInventory(int size, ItemStack container, String playerUUID) {
        this.list = DefaultedList.ofSize(size, ItemStack.EMPTY);
        this.size = size;
        this.container = container;
        playerFile = MOD_CONFIG_DIR.resolve("backpacks").resolve(playerUUID + ".dat").toFile();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.list) {
            if (!stack.isEmpty()) return false;
        }

        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.list.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.list, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        final ItemStack stack = this.list.remove(slot);
        this.setStack(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.list.set(slot, stack);
    }

    @Override
    public void markDirty() {
        this.write();
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.read();
    }

    @Override
    public void onClose(PlayerEntity player) {
        this.write();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    public void write() {
        if (this.container != null) {
            var nbtCompound = container.getOrCreateNbt();
            nbtCompound.put("BackpackInventory", Inventories.writeNbt(new NbtCompound(), list, true));
            try {
                NbtIo.write(nbtCompound, playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void read() {
        if (this.container != null) {
            try {
                var nbtCompound = NbtIo.read(playerFile);
                if (nbtCompound == null) return;
                Inventories.readNbt(nbtCompound.getCompound("BackpackInventory"), this.list);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
