package dev.the_fireplace.overlord.client.model;

import dev.the_fireplace.overlord.Overlord;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class OverlordModelLayers
{
    public static EntityModelLayer OWNED_SKELETON_MODEL = createModelLayer("standard_owned_skeleton");
    public static EntityModelLayer MUSCLE_OWNED_SKELETON_MODEL = createModelLayer("muscle_owned_skeleton");
    public static EntityModelLayer SLIM_OWNED_SKELETON_MODEL = createModelLayer("slim_owned_skeleton");
    public static EntityModelLayer SLIM_MUSCLE_OWNED_SKELETON_MODEL = createModelLayer("slim_muscle_owned_skeleton");
    public static EntityModelLayer OWNED_SKELETON_LEGGINGS_MODEL = createModelLayer("owned_skeleton_leggings");
    public static EntityModelLayer MUSCLE_OWNED_SKELETON_LEGGINGS_MODEL = createModelLayer("muscle_owned_skeleton_leggings");

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(OWNED_SKELETON_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(Dilation.NONE, false, false, false));
        EntityModelLayerRegistry.registerModelLayer(MUSCLE_OWNED_SKELETON_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(Dilation.NONE, true, false, false));
        EntityModelLayerRegistry.registerModelLayer(SLIM_OWNED_SKELETON_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(Dilation.NONE, false, false, true));
        EntityModelLayerRegistry.registerModelLayer(SLIM_MUSCLE_OWNED_SKELETON_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(Dilation.NONE, true, false, true));
        EntityModelLayerRegistry.registerModelLayer(OWNED_SKELETON_LEGGINGS_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(Dilation.NONE, false, true, false));
        EntityModelLayerRegistry.registerModelLayer(MUSCLE_OWNED_SKELETON_LEGGINGS_MODEL, () -> OwnedSkeletonModel.getTexturedModelData(Dilation.NONE, true, true, false));
    }

    private static EntityModelLayer createModelLayer(String name) {
        return new EntityModelLayer(new Identifier(Overlord.MODID, name), name);
    }
}
