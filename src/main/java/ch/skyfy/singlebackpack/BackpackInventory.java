package ch.skyfy.singlebackpack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;

public class BackpackInventory implements Inventory {

    private DefaultedList<ItemStack> list;
    private final int size;

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
        this.write(null);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.read(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        this.write(player);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.list.clear();
    }

    public void write(PlayerEntity player) {
        if (this.container != null) {
            player = MinecraftClient.getInstance().player;
            if(player != null){
                var nbt = new NbtCompound();
                nbt.put("BackpackInventory", Inventories.writeNbt(nbt, this.list, true));

                player.writeNbt(nbt);
                player.writeCustomDataToNbt(nbt);
                player.saveNbt(nbt);
            }else System.out.println("Player is null");
//            this.container.getOrCreateNbt().put("BackpackInventory", Inventories.writeNbt(new NbtCompound(), this.list, true));
        }
    }

    public void read(PlayerEntity player) {
        if (this.container != null) {
            if(player != null){
                // How get NbtCompounds.getCompound("BackpackInventory")
                for (NbtElement nbtElement : player.getAttributes().toNbt()) {
                    System.out.println("nbtElement " + nbtElement.asString());
                    System.out.println("getNbtType " + nbtElement.getNbtType().toString());
                }
            }
//            Inventories.readNbt(this.container.getOrCreateNbt().getCompound("BackpackInventory"), this.list);
        }
    }
}
