package ch.skyfy.singlebackpack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;

public class BackpackInventory implements Inventory {

    private DefaultedList<ItemStack> list;
    private int size;

    private final ItemStack container;
    // backpack where nbt-data will be written

    public BackpackInventory(int size, ItemStack container) {
        this.list = DefaultedList.ofSize(size, ItemStack.EMPTY);
        this.size = size;
        this.container = container;
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
            this.container.getOrCreateNbt().put("BackpackInventory", Inventories.writeNbt(new NbtCompound(), this.list, true));
        }
    }

    public void read() {
        if (this.container != null) {
            Inventories.readNbt(this.container.getOrCreateNbt().getCompound("BackpackInventory"), this.list);
        }
    }
}
