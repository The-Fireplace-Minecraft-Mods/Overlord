package dev.the_fireplace.overlord.client.model;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.loader.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.resources.ResourceLocation;

public final class OverlordModelLayers
{
    public static ModelLayerLocation OWNED_SKELETON_MODEL = createModelLayer("standard_owned_skeleton");
    public static ModelLayerLocation MUSCLE_OWNED_SKELETON_MODEL = createModelLayer("muscle_owned_skeleton");
    public static ModelLayerLocation SLIM_OWNED_SKELETON_MODEL = createModelLayer("slim_owned_skeleton");
    public static ModelLayerLocation SLIM_MUSCLE_OWNED_SKELETON_MODEL = createModelLayer("slim_muscle_owned_skeleton");
    public static ModelLayerLocation OWNED_SKELETON_LEGGINGS_MODEL = createModelLayer("owned_skeleton_leggings");
    public static ModelLayerLocation MUSCLE_OWNED_SKELETON_LEGGINGS_MODEL = createModelLayer("muscle_owned_skeleton_leggings");

    public static void register() {
        ModelLayerRegistry registry = OverlordConstants.getInjector().getInstance(ModelLayerRegistry.class);
        registry.register(OWNED_SKELETON_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(CubeDeformation.NONE, false, false, false));
        registry.register(MUSCLE_OWNED_SKELETON_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(CubeDeformation.NONE, true, false, false));
        registry.register(SLIM_OWNED_SKELETON_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(CubeDeformation.NONE, false, false, true));
        registry.register(SLIM_MUSCLE_OWNED_SKELETON_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(CubeDeformation.NONE, true, false, true));
        registry.register(OWNED_SKELETON_LEGGINGS_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(CubeDeformation.NONE, false, true, false));
        registry.register(MUSCLE_OWNED_SKELETON_LEGGINGS_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(CubeDeformation.NONE, true, true, false));
    }

    private static ModelLayerLocation createModelLayer(String name) {
        return new ModelLayerLocation(new ResourceLocation(OverlordConstants.MODID, name), name);
    }
}
