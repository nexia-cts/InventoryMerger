package net.blumbo.inventorymerger;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Iterator;

public class InventoryMergerImpl {

    protected static void merge(ServerPlayerEntity player, ItemStack[] layout, ItemStack[] items) {

        PlayerInventory inv = player.getInventory();
        inv.clear();

        ArrayList<ItemStack> itemBulk = arrayToBulk(items);

        mergeMatches(inv, layout, itemBulk);
        mergeNotMatched(inv, itemBulk);
    }

    private static ArrayList<ItemStack> arrayToBulk(ItemStack[] itemArray) {
        ArrayList<ItemStack> itemList = new ArrayList<>();

        for (ItemStack arrayStack : itemArray) {
            if (arrayStack == null || arrayStack.isEmpty()) continue;

            boolean matchFound = false;
            for (ItemStack listStack : itemList) {
                if (!ItemStack.canCombine(arrayStack, listStack)) continue;
                matchFound = true;
                listStack.increment(arrayStack.getCount());
            }

            if (!matchFound) {
                itemList.add(arrayStack.copy());
            }
        }
        return itemList;
    }

    private static void mergeMatches(PlayerInventory inv, ItemStack[] layout, ArrayList<ItemStack> itemsToAdd) {
        for (int i = 0; i < layout.length; i++) {
            int slot = getPrioritySlot(i);
            ItemStack layoutStack = layout[slot];
            if (layoutStack == null) continue;
            layoutStack = layoutStack.copy();

            Iterator<ItemStack> it = itemsToAdd.iterator();
            while (it.hasNext()) {
                ItemStack stackToAdd = it.next();
                if (!ItemStack.canCombine(layoutStack, stackToAdd)) continue;

                int count = Math.min(layoutStack.getCount(), stackToAdd.getCount());
                ItemStack newStack = new ItemStack(stackToAdd.getItem(), count);
                if (stackToAdd.getNbt() != null) newStack.setNbt(stackToAdd.getNbt().copy());
                inv.setStack(slot, newStack);

                stackToAdd.decrement(count);
                if (stackToAdd.getCount() <= 0) it.remove();
            }
        }
    }

    private static void mergeNotMatched(PlayerInventory inv, ArrayList<ItemStack> itemsToAdd) {
        for (int i = 0; i < itemsToAdd.size(); i++) {
            ItemStack itemStack = itemsToAdd.get(i);
            inv.insertStack(itemStack);
        }
    }

    private static int getPrioritySlot(int index) {
        if (index < 4) return index + 36; // Armor slots first
        if (index < 13) return index - 4; // Hotbar second
        if (index == 13) return 40; // Offhand third
        else return index - 5; // Rest of inventory last
    }

}
