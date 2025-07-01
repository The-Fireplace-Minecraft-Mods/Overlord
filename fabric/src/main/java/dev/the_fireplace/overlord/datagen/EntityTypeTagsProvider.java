package dev.the_fireplace.overlord.datagen;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class EntityTypeTagsProvider extends FabricTagProvider.EntityTypeTagProvider
{
    public EntityTypeTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        OverlordEntities overlordEntities = OverlordConstants.getInjector().getInstance(OverlordEntities.class);
        //TODO find out what exactly tagging the owned skeletons with this does
        this.getOrCreateTagBuilder(EntityTypeTags.SKELETONS).add(overlordEntities.getOwnedSkeletonType());
    }
}
