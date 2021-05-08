package dev.the_fireplace.overlord.init;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public final class OverlordEntities {
    public static final Identifier OWNED_SKELETON_ID = new Identifier(Overlord.MODID, "owned_skeleton");
    @SuppressWarnings("deprecation")
    public static final EntityType<OwnedSkeletonEntity> OWNED_SKELETON_TYPE =
        Registry.register(Registry.ENTITY_TYPE, OWNED_SKELETON_ID,
            FabricEntityTypeBuilder.create(EntityCategory.MISC, OwnedSkeletonEntity::new)
                .dimensions(EntityDimensions.changing(1, 2))
                .trackable(24, 3, true)
                .build()
        );
    public static void register() {
        ContainerProviderRegistry.INSTANCE.registerFactory(OWNED_SKELETON_ID, (syncId, identifier, player, buf) -> {
            final World world = player.world;
            final UUID skeletonId = buf.readUuid();
            List<Entity> entities = world.getEntities(player, player.getBoundingBox().expand(6), e -> e instanceof OwnedSkeletonEntity && e.getUuid().equals(skeletonId));
            return ((OwnedSkeletonEntity)entities.get(0)).getContainer(player.inventory, syncId);
        });
    }
}
