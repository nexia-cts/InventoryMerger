package net.blumbo.inventorymerger;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryMerger implements ModInitializer {

    @Override
    public void onInitialize() {}

    public static void merge(ServerPlayerEntity player, ItemStack[] layout, ItemStack[] items) {
        InventoryMergerImpl.merge(player, layout, items);
    }

}
