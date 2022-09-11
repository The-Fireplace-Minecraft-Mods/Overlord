package dev.the_fireplace.overlord.domain.entity.creation;

import net.minecraft.world.item.ItemStack;

public interface SkeletonIngredient
{
    boolean matches(ItemStack stack);

    int getRequiredCount();
}
