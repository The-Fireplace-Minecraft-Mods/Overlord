package dev.the_fireplace.overlord.entity.creation.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public class ItemIngredient extends AbstractIngredient
{
    private final Item item;
    @Nullable
    private CompoundTag compoundTag = null;

    public ItemIngredient(Item item) {
        this.item = item;
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (!stack.getItem().equals(item)) {
            return false;
        }
        if (compoundTag != null && !stack.hasTag()) {
            return false;
        }
        if (compoundTag != null && !compoundTag.equals(stack.getTag())) {
            return false;
        }

        return true;
    }

    public void setCompoundTag(@Nullable CompoundTag CompoundTag) {
        this.compoundTag = CompoundTag;
    }
}
