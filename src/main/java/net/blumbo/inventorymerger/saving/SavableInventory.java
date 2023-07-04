package net.blumbo.inventorymerger.saving;

import com.google.gson.Gson;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SavableInventory {

    private static final Gson gson = new Gson();

    public SavableItemStack[] content;

    public SavableInventory(PlayerInventory inventory) {
        this.content = new SavableItemStack[inventory.size()];

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack.isEmpty()) continue;
            content[i] = new SavableItemStack(itemStack);
        }
    }

    public SavableInventory(ItemStack[] items) {
        this.content = new SavableItemStack[items.length];

        for (int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];
            if (itemStack == null || itemStack.isEmpty()) continue;
            content[i] = new SavableItemStack(itemStack);
        }
    }

    public PlayerInventory asPlayerInventory() {
        PlayerInventory inventory = new PlayerInventory(null);
        int loops = Math.min(inventory.size(), this.content.length);

        for (int i = 0; i < loops; i++) {
            SavableItemStack savableItemStack = content[i];
            if (savableItemStack == null) continue;
            inventory.setStack(i, savableItemStack.toItemStack());
        }

        return inventory;
    }

    public ItemStack[] asItemArray() {
        ItemStack[] itemArray = new ItemStack[this.content.length];

        for (int i = 0; i < this.content.length; i++) {
            SavableItemStack savableItemStack = content[i];
            if (savableItemStack == null) continue;
            itemArray[i] = savableItemStack.toItemStack();
        }

        return itemArray;
    }

    @Nullable
    public static SavableInventory fromSave(String string) {
        try {
            return gson.fromJson(string, SavableInventory.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toSave() {
        return gson.toJson(this);
    }

}
