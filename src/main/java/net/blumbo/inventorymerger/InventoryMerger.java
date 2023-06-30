package net.blumbo.inventorymerger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryMerger implements ModInitializer {

    @Override
    public void onInitialize() {}

    /**
     * Give a player specified items with specified inventory layout.
     * @param player Player who will be given the items. Their inventory will be cleared first!
     * @param layout Inventory layout which the given items will attempt to follow.
     * @param items Given items.
     */
    public static void merge(ServerPlayerEntity player, ItemStack[] layout, ItemStack[] items) {
        InventoryMergerImpl.merge(player, layout, items);
    }

    /**
     * A utility method to copy ItemStacks from a PlayerInventory to an Array.
     * @param inv Source inventory.
     * @return Array of copied ItemStacks.
     */
    public static ItemStack[] invToItemArray(PlayerInventory inv) {
        ItemStack[] items = new ItemStack[inv.size()];
        for (int i = 0; i < items.length; i++) {
            ItemStack itemStack = inv.getStack(i);
            if (itemStack.isEmpty()) continue;
            items[i] = itemStack.copy();
        }
        return items;
    }

}
