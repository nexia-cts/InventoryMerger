package net.blumbo.inventorymerger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryMerger implements ModInitializer {

    @Override
    public void onInitialize() {}

    public static void merge(ServerPlayerEntity player, ItemStack[] layout, ItemStack[] items) {
        InventoryMergerImpl.merge(player, layout, items);
    }

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
