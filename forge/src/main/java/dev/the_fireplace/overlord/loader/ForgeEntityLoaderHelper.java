package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegisterEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Implementation
public final class ForgeEntityLoaderHelper implements EntityLoaderHelper
{
    private MenuType<OwnedSkeletonContainer> ownedSkeletonMenuType;

    @Override
    public EntityType<OwnedSkeletonEntity> buildOwnedSkeletonType() {
        EntityType<OwnedSkeletonEntity> ownedSkeleton = EntityType.Builder.of(OwnedSkeletonEntity::new, MobCategory.MISC)
            .sized(0.6F, 1.99F)
            .setTrackingRange(24)
            .setUpdateInterval(3)
            .setShouldReceiveVelocityUpdates(true)
            .build("owned_skeleton");

        FMLJavaModLoadingContext.get().getModEventBus().addListener(new AttributeCreationHandler<>(ownedSkeleton, OwnedSkeletonEntity::createOwnedSkeletonAttributes)::onAttributeCreation);

        return ownedSkeleton;
    }

    @Override
    public MenuType<OwnedSkeletonContainer> registerOwnedSkeletonMenuType() {
        ownedSkeletonMenuType = new MenuType<>((IContainerFactory<OwnedSkeletonContainer>) (windowId, inv, data) -> {
            final Player player = inv.player;
            final Level world = player.level;
            final UUID skeletonId = data.readUUID();
            List<Entity> entities = world.getEntities(player, player.getBoundingBox().inflate(6), e -> e instanceof OwnedSkeletonEntity && e.getUUID().equals(skeletonId));
            return ((OwnedSkeletonEntity) entities.get(0)).getContainer(player.getInventory(), windowId);
        });

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerMenus);

        return ownedSkeletonMenuType;
    }

    public void registerMenus(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registry.MENU_REGISTRY)) {
            return;
        }
        event.register(Registry.MENU_REGISTRY, OverlordEntities.OWNED_SKELETON_ID, () -> ownedSkeletonMenuType);
    }

    private static class AttributeCreationHandler<T extends LivingEntity>
    {
        private final EntityType<T> entityType;
        private final Supplier<AttributeSupplier.Builder> attributeSupplierBuilder;

        AttributeCreationHandler(EntityType<T> entityType, Supplier<AttributeSupplier.Builder> attributeSupplierBuilder) {
            this.entityType = entityType;
            this.attributeSupplierBuilder = attributeSupplierBuilder;
        }

        public void onAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(entityType, attributeSupplierBuilder.get().build());
        }
    }
}
