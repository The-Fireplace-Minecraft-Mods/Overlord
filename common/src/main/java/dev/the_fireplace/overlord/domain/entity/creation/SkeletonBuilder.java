package dev.the_fireplace.overlord.domain.entity.creation;

import dev.the_fireplace.overlord.domain.blockentity.Tombstone;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;

public interface SkeletonBuilder
{
    boolean canBuildWithIngredients(Container inventory);

    OwnedSkeletonEntity build(Container inventory, Level world, Tombstone tombstone);
}
