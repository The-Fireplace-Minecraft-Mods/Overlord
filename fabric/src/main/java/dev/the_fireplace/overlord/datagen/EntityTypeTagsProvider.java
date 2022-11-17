package dev.the_fireplace.overlord.datagen;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tags.EntityTypeTags;

public class EntityTypeTagsProvider extends FabricTagProvider.EntityTypeTagProvider
{
    public EntityTypeTagsProvider(FabricDataGenerator root) {
        super(root);
    }

    @Override
    protected void generateTags() {
        OverlordEntities overlordEntities = OverlordConstants.getInjector().getInstance(OverlordEntities.class);
        //TODO find out what exactly tagging the owned skeletons with this does
        this.tag(EntityTypeTags.SKELETONS).add(overlordEntities.getOwnedSkeletonType());
    }
}
