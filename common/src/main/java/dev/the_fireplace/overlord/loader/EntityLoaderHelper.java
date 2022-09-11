package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;

public interface EntityLoaderHelper
{
    EntityType<OwnedSkeletonEntity> buildOwnedSkeletonType();

    MenuType<OwnedSkeletonContainer> registerOwnedSkeletonMenuType();
}
