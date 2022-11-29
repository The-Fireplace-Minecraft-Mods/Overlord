package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.UUID;

@Implementation
public final class FabricEntityLoaderHelper implements EntityLoaderHelper
{
    @Override
    public EntityType<OwnedSkeletonEntity> buildOwnedSkeletonType() {
        return FabricEntityTypeBuilder.createLiving()
            .entityFactory(OwnedSkeletonEntity::new)
            .defaultAttributes(OwnedSkeletonEntity::createOwnedSkeletonAttributes)
            .dimensions(EntityDimensions.scalable(0.6F, 1.99F))
            .trackable(24, 3, true)
            .build();
    }

    @Override
    public MenuType<OwnedSkeletonContainer> registerOwnedSkeletonMenuType() {
        ScreenHandlerRegistry.ExtendedClientHandlerFactory<OwnedSkeletonContainer> ownedSkeletonContainerClientHandlerFactory = (syncId, playerInventory, buf) -> {
            final Player player = playerInventory.player;
            final Level world = player.level;
            final UUID skeletonId = buf.readUUID();
            List<Entity> entities = world.getEntities(player, player.getBoundingBox().inflate(6), e -> e instanceof OwnedSkeletonEntity && e.getUUID().equals(skeletonId));
            return ((OwnedSkeletonEntity) entities.get(0)).getContainer(player.inventory, syncId);
        };
        return ScreenHandlerRegistry.registerExtended(OverlordEntities.OWNED_SKELETON_ID, ownedSkeletonContainerClientHandlerFactory);
    }
}
