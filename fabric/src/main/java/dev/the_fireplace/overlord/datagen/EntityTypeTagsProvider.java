package dev.the_fireplace.overlord.datagen;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.nio.file.Path;

public class EntityTypeTagsProvider extends TagsProvider<EntityType<?>>
{
    public EntityTypeTagsProvider(DataGenerator root) {
        super(root, Registry.ENTITY_TYPE);
    }

    @Override
    protected void addTags() {
        OverlordEntities overlordEntities = OverlordConstants.getInjector().getInstance(OverlordEntities.class);
        //TODO find out what exactly tagging the owned skeletons with this does
        this.tag(EntityTypeTags.SKELETONS).add(overlordEntities.getOwnedSkeletonType());
    }

    @Override
    protected Path getPath(ResourceLocation identifier) {
        return this.generator.getOutputFolder().resolve("data/" + identifier.getNamespace() + "/tags/entity_types/" + identifier.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Overlord Entity Types";
    }
}
