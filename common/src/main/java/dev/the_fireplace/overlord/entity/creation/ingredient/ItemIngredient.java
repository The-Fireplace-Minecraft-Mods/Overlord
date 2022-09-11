package dev.the_fireplace.overlord.entity.creation.ingredient;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ItemIngredient extends AbstractIngredient
{
    private final Item item;
    @Nullable
    private CompoundTag nbtCompound = null;

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

    public Item getItem() {
        return item;
    }

    public void setNbtCompound(@Nullable CompoundTag nbtCompound) {
        this.nbtCompound = nbtCompound;
    }
}
