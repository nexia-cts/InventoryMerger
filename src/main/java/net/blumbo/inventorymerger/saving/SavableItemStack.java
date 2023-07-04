package net.blumbo.inventorymerger.saving;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class SavableItemStack {

    public String item;
    public String nbt;
    public int count;

    public SavableItemStack(ItemStack itemStack) {
        this.item = Registries.ITEM.getId(itemStack.getItem()).toString();
        this.nbt = itemStack.getNbt() == null ? null : itemStack.getNbt().asString();
        this.count = itemStack.getCount();
    }

    public ItemStack toItemStack() {
        Item item = Registries.ITEM.get(new Identifier(this.item));
        ItemStack itemStack = new ItemStack(item, count);

        if (this.nbt != null) {
            try {
                NbtCompound nbtCompound = StringNbtReader.parse(this.nbt);
                itemStack.setNbt(nbtCompound);
            } catch (CommandSyntaxException ignored) {}
        }

        return itemStack;
    }

}
