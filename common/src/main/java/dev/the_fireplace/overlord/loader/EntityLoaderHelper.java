package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.minecraft.world.entity.EntityType;

public interface EntityLoaderHelper
{
    EntityType<OwnedSkeletonEntity> buildOwnedSkeletonType();

    void registerOwnedSkeletonMenuType();
}
