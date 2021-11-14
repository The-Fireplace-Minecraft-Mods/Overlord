package dev.the_fireplace.overlord.domain.entity.creation;

import dev.the_fireplace.overlord.domain.blockentity.Tombstone;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.world.World;

public interface SkeletonBuilder
{
    boolean canBuildWithIngredients(Inventory inventory);

    OwnedSkeletonEntity build(Inventory inventory, World world, Tombstone tombstone);
}
