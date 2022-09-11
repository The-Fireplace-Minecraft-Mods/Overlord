package dev.the_fireplace.overlord.datapack;

import dev.the_fireplace.lib.api.io.injectables.JsonFileReader;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.creation.SkeletonRecipeRegistryImpl;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

import javax.inject.Inject;

public final class SkeletonBuildingPackLoader extends SkeletonBuildingReloadListener implements SimpleSynchronousResourceReloadListener
{
    @Inject
    public SkeletonBuildingPackLoader(SkeletonRecipeRegistryImpl skeletonRecipeRegistry, JsonFileReader jsonFileReader) {
        super(skeletonRecipeRegistry, jsonFileReader);
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(OverlordConstants.MODID, "skeleton_building");
    }
}
