package ch.skyfy.singlebackpack.client.screen;

import ch.skyfy.singlebackpack.BackpacksManager;
import ch.skyfy.singlebackpack.SingleBackpack;
import ch.skyfy.singlebackpack.client.ClientSetup;
import ch.skyfy.singlebackpack.client.screen.slot.BackpackSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.apache.commons.compress.archivers.sevenz.CLI;


//the screen handler takes care of syncing the player inventory
//with the container
public class BackpackScreenHandler extends ScreenHandler {

    private final Inventory inventory;//your actual inventory

//    private String uuid;

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(BackpacksManager.playerRows.get(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? ClientSetup.playerClientUUID : playerInventory.player.getUuidAsString()) * 9));//9 * 6 slots
    }

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(SingleBackpack.BACKPACK_SCREEN_HANDLER, syncId);

        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
            System.out.println("CLIENT getEnvironmentType ");
            System.out.println("playerInventory " + playerInventory.player.getUuidAsString());
        }

        this.inventory = inventory;
        this.buildContainer(playerInventory);
        this.inventory.onOpen(playerInventory.player);//calls onOpen() from our inventory to readNbt
    }

    //Create slots for the backpack
    public void buildContainer(PlayerInventory playerInventory) {

        String uuid = "";
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
            System.out.println("CLIENT CLIENT CLIENT");
            BackpacksManager.playerRows.forEach((s, aByte) -> {
                System.out.println("UUID + " + s + " value + " + aByte);
            });
            uuid = ClientSetup.playerClientUUID;
        }else{
            System.out.println("SERVER SERVR");
            BackpacksManager.playerRows.forEach((s, aByte) -> {
                System.out.println("UUID + " + s + " value + " + aByte);
            });
            uuid = playerInventory.player.getUuidAsString();
        }
        System.out.println("UUID " + uuid);

        int i = (BackpacksManager.playerRows.get(uuid) - 4) * 18;

        int j;
        int k;
        //container
        for (j = 0; j < BackpacksManager.playerRows.get(uuid); ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new BackpackSlot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        //player inventory
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 161 + i));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;//if this is false the player cannot access the screen
    }

    //calls the onClose() from our inventory to write nbt
    @Override
    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }

    //Called when you try to insert an item from x to y inventory
    //this is a simple implementation to allow that
    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.getSlot(index);
        if (slot != null && slot.hasStack()) {
            final ItemStack stack2 = slot.getStack();
            stack = stack2.copy();
            if (index < this.inventory.size()) {
                if (!this.insertItem(stack2, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(stack2, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (stack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return stack;
    }

}
