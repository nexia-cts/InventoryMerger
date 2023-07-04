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
     * @param dropLeftover Drop items that won't fit into the layout.
     *                     Items that don't fit will be lost if set to false.
     */
    public static void merge(ServerPlayerEntity player, ItemStack[] layout, ItemStack[] items, boolean dropLeftover) {
        InventoryMergerImpl.merge(player, layout, items, dropLeftover, false);
    }

    /**
     * Makes sure all items fit into the inventory by stacking them up immediately, instead of exactly matching
     * item amounts in provided layout.
     */
    public static void mergeSafe(ServerPlayerEntity player, ItemStack[] layout, ItemStack[] items) {
        InventoryMergerImpl.merge(player, layout, items, false, true);
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
