package dev.the_fireplace.overlord.entity;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.datastructure.SingletonFactory;
import dev.the_fireplace.overlord.loader.EntityLoaderHelper;
import dev.the_fireplace.overlord.loader.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class OverlordEntities
{
    public static final ResourceLocation OWNED_SKELETON_ID = new ResourceLocation(OverlordConstants.MODID, "owned_skeleton");
    private final EntityLoaderHelper entityLoaderHelper;
    private final SingletonFactory<EntityType<OwnedSkeletonEntity>> ownedSkeletonType;
    private MenuType<OwnedSkeletonContainer> ownedSkeletonScreenHandler;

    private RegistryHelper<EntityType<?>> entityRegistry = (id, value) -> Registry.register(Registry.ENTITY_TYPE, id, value);

    @Inject
    public OverlordEntities(EntityLoaderHelper entityLoaderHelper) {
        this.entityLoaderHelper = entityLoaderHelper;
        this.ownedSkeletonType = new SingletonFactory<>(entityLoaderHelper::buildOwnedSkeletonType);
    }

    public void register() {
        entityRegistry.register(OWNED_SKELETON_ID, ownedSkeletonType.get());
        ownedSkeletonScreenHandler = entityLoaderHelper.registerOwnedSkeletonMenuType();
    }

    public void setEntityRegistry(RegistryHelper<EntityType<?>> entityRegistry) {
        this.entityRegistry = entityRegistry;
    }

    public EntityType<OwnedSkeletonEntity> getOwnedSkeletonType() {
        return ownedSkeletonType.get();
    }

    public MenuType<OwnedSkeletonContainer> getOwnedSkeletonScreenHandler() {
        return ownedSkeletonScreenHandler;
    }
}
