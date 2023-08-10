package io.github.blumbo.inventorymerger.saving;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SavableItemStack {

    public String item;
    public String nbt;
    public int count;

    public SavableItemStack(ItemStack itemStack) {
        this.item = Registry.ITEM.getId(itemStack.getItem()).toString();
        this.nbt = itemStack.getOrCreateTag() == null ? null : itemStack.getOrCreateTag().asString();
        this.count = itemStack.getCount();
    }

    public ItemStack toItemStack() {
        Item item = Registry.ITEM.get(new Identifier(this.item));
        ItemStack itemStack = new ItemStack(item, count);

        if (this.nbt != null) {
            try {
                CompoundTag nbtCompound = StringNbtReader.parse(this.nbt);
                itemStack.setTag(nbtCompound);
            } catch (CommandSyntaxException ignored) {}
        }

        return itemStack;
    }

}
