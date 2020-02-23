package the_fireplace.overlord.fabric.init;

import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.OverlordHelper;
import the_fireplace.overlord.fabric.entity.OwnedSkeletonEntity;

public class OverlordEntities {
    public static final EntityType<OwnedSkeletonEntity> OWNED_SKELETON_TYPE =
        Registry.register(Registry.ENTITY_TYPE, new Identifier(OverlordHelper.MODID, "owned_skeleton"),
            FabricEntityTypeBuilder.create(EntityCategory.MISC, OwnedSkeletonEntity::new).size(EntityDimensions.changing(1, 2)).build());
}
