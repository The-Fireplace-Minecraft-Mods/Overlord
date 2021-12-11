package dev.the_fireplace.overlord.entity.creation.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import javax.annotation.Nullable;

public class ItemIngredient extends AbstractIngredient
{
    private final Item item;
    @Nullable
    private NbtCompound nbtCompound = null;

    public ItemIngredient(Item item) {
        this.item = item;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (!stack.getItem().equals(item)) {
            return false;
        }
        if (nbtCompound != null && !stack.hasTag()) {
            return false;
        }
        if (nbtCompound != null && !nbtCompound.equals(stack.getTag())) {
            return false;
        }

        return true;
    }

    public void setNbtCompound(@Nullable NbtCompound nbtCompound) {
        this.nbtCompound = nbtCompound;
    }
}
