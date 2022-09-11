package dev.the_fireplace.overlord.domain.world;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

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
