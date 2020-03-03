package the_fireplace.overlord.fabric.init;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import the_fireplace.overlord.OverlordHelper;
import the_fireplace.overlord.fabric.entity.OwnedSkeletonEntity;

import java.util.List;
import java.util.UUID;

public class OverlordEntities {
    public static final Identifier OWNED_SKELETON_ID = new Identifier(OverlordHelper.MODID, "owned_skeleton");
    public static final EntityType<OwnedSkeletonEntity> OWNED_SKELETON_TYPE =
        Registry.register(Registry.ENTITY_TYPE, OWNED_SKELETON_ID,
            FabricEntityTypeBuilder.create(EntityCategory.MISC, OwnedSkeletonEntity::new).size(EntityDimensions.changing(1, 2)).build());
    public static void register() {
        ContainerProviderRegistry.INSTANCE.registerFactory(OWNED_SKELETON_ID, (syncId, identifier, player, buf) -> {
            final World world = player.world;
            final UUID skeletonId = buf.readUuid();
            List<Entity> entities = world.getEntities(player, player.getBoundingBox().expand(6), e -> e instanceof OwnedSkeletonEntity && e.getUuid().equals(skeletonId));
            return ((OwnedSkeletonEntity)entities.get(0)).getContainer(player.inventory);
        });
    }
}
