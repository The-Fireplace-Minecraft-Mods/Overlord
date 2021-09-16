package dev.the_fireplace.overlord.domain.world;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Creates an item entity in the world based on a living entity. Does not remove the given stack from the entity's inventory.
 */
public interface ItemDropper {
    @Nullable
    ItemEntity dropItem(ItemStack stack, LivingEntity entity);
    @Nullable
    ItemEntity throwItem(ItemStack stack, LivingEntity entity);
}
