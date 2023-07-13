package io.github.blumbo.inventorymerger;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;

public class InventoryMergerImpl {

    protected static void merge(ServerPlayerEntity player, ItemStack[] layout, ItemStack[] items,
                                boolean dropLeftover, boolean safeMerge) {

        PlayerInventory inv = player.getInventory();
        inv.clear();

        ArrayList<ItemBulk> itemBulks = itemArrayToBulk(items);

        mergeToGivenLayout(inv, layout, itemBulks, safeMerge);
        addNormally(inv, itemBulks, true);
        mergeToKitLayout(inv, itemBulks);
        addNormally(inv, itemBulks, false);

        if (dropLeftover) dropLeftovers(player, itemBulks);
    }

    private static void mergeToGivenLayout(PlayerInventory inv, ItemStack[] layout, ArrayList<ItemBulk> itemBulks,
                                           boolean safeMerge) {
        for (int i = 0; i < layout.length; i++) {
            int slot = getPrioritySlot(i);
            ItemStack layoutStack = layout[slot];
            if (layoutStack == null) continue;
            layoutStack = layoutStack.copy();

            Iterator<ItemBulk> it = itemBulks.iterator();
            while (it.hasNext()) {
                ItemBulk itemBulk = it.next();
                ItemStack stackToAdd = itemBulk.itemStack;
                if (!ItemStack.canCombine(layoutStack, stackToAdd)) continue;

                int count;
                if (safeMerge) count = Math.min(itemBulk.itemStack.getMaxCount(), itemBulk.getCount());
                else count = Math.min(layoutStack.getCount(), itemBulk.getCount());

                ItemStack newStack = stackToAdd.copy();
                newStack.setCount(count);
                inv.setStack(slot, newStack);

                itemBulk.decrement(count);
                if (itemBulk.getCount() <= 0) it.remove();
            }
        }
    }

    private static void mergeToKitLayout(PlayerInventory inv, ArrayList<ItemBulk> itemBulks) {
        for (ItemBulk bulk : itemBulks) {
            for (ItemBulk.Slot bulkSlot : bulk.countPerSlot) {
                if (bulk.getCount() < 1) continue;
                if (!inv.getStack(bulkSlot.slot).isEmpty()) continue;

                int amount = Math.min(bulkSlot.count, bulk.getCount());

                ItemStack itemStack = bulk.itemStack.copy();
                itemStack.setCount(amount);
                inv.setStack(bulkSlot.slot, itemStack);

                bulk.decrement(amount);
            }
        }
    }

    private static void dropLeftovers(ServerPlayerEntity player, ArrayList<ItemBulk> itemBulks) {
        for (ItemBulk bulk : itemBulks) {
            ItemStack bulkStack = bulk.itemStack;
            while (bulkStack.getCount() > 0) {
                if (bulkStack.getCount() <= bulkStack.getMaxCount()) {
                    spawnItem(player, bulkStack);
                    break;
                }
                ItemStack itemStack = bulkStack.copy();
                itemStack.setCount(itemStack.getMaxCount());
                bulkStack.decrement(itemStack.getCount());
                spawnItem(player, itemStack);
            }
        }
    }

    private static void spawnItem(ServerPlayerEntity player, ItemStack itemStack) {
        Vec3d pos = player.getEyePos().add(0, -0.3, 0);
        ItemEntity itemEntity = new ItemEntity(player.getServerWorld(), pos.x, pos.y, pos.z, itemStack,
            0, 0, 0);
        player.getServerWorld().spawnEntity(itemEntity);
    }

    private static void addNormally(PlayerInventory inv, ArrayList<ItemBulk> itemBulks, boolean addToExistingStacks) {
        for (ItemBulk bulk : itemBulks) {
            addItemToInv(inv, bulk.itemStack, addToExistingStacks);
        }
    }

    private static void addItemToInv(PlayerInventory inv, ItemStack bulkStack, boolean addToExistingStack) {
        for (int i = 0; i < 37; i++) {
            int slot = getNonArmorPrioritySlot(i);
            ItemStack invStack = inv.getStack(slot);
            int count;

            if (addToExistingStack) {
                if (invStack.isEmpty()) continue;
                if (!ItemStack.canCombine(bulkStack, invStack)) continue;

                count = Math.min(bulkStack.getCount(), bulkStack.getMaxCount() - invStack.getCount());
                invStack.increment(count);

            } else {
                if (!invStack.isEmpty()) continue;

                count = Math.min(bulkStack.getMaxCount(), bulkStack.getCount());
                ItemStack givenStack = bulkStack.copy();
                givenStack.setCount(count + invStack.getCount());
                inv.setStack(slot, givenStack);
            }

            bulkStack.decrement(count);
        }
    }

    // Turns array of ItemStacks into a list while combining their amounts.
    private static ArrayList<ItemBulk> itemArrayToBulk(ItemStack[] itemArray) {
        ArrayList<ItemBulk> bulkList = new ArrayList<>();

        for (int i = 0; i < itemArray.length; i++) {
            int slot = getPrioritySlot(i);
            ItemStack arrayStack = itemArray[slot];
            if (arrayStack == null || arrayStack.isEmpty()) continue;

            boolean matchFound = false;
            for (ItemBulk itemBulk : bulkList) {
                if (!ItemStack.canCombine(arrayStack, itemBulk.itemStack)) continue;
                matchFound = true;
                itemBulk.addSlot(slot, arrayStack.getCount());
            }

            if (!matchFound) {
                bulkList.add(new ItemBulk(arrayStack.copy(), slot));
            }
        }
        return bulkList;
    }

    private static int getPrioritySlot(int index) {
        if (index < 4) return index + 36; // Armor slots first
        if (index < 13) return index - 4; // Hotbar second
        if (index == 13) return 40; // Offhand third
        else return index - 5; // Rest of inventory last
    }

    private static int getNonArmorPrioritySlot(int index) {
        if (index < 9) return index; // Hotbar first
        if (index == 9) return 40; // Offhand second
        else return index - 1; // Rest of inventory last
    }

    private static class ItemBulk {

        private final ItemStack itemStack;
        private final ArrayList<Slot> countPerSlot;

        private ItemBulk(ItemStack itemStack, int firstSlot) {
            this.itemStack = itemStack;
            countPerSlot = new ArrayList<>(1);
            countPerSlot.add(new Slot(firstSlot, itemStack.getCount()));
        }

        private void addSlot(int slot, int count) {
            itemStack.increment(count);
            countPerSlot.add(new Slot(slot, count));
        }

        private void decrement(int amount) {
            this.itemStack.decrement(amount);
        }

        private int getCount() {
            return itemStack.getCount();
        }

        private record Slot(int slot, int count) {}

    }

}
