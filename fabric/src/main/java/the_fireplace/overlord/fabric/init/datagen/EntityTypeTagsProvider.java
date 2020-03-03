package the_fireplace.overlord.fabric.init.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.fabric.init.OverlordEntities;

import java.nio.file.Path;

public class EntityTypeTagsProvider extends AbstractTagProvider<EntityType<?>> {
    public EntityTypeTagsProvider(DataGenerator root) {
        super(root, Registry.ENTITY_TYPE);
    }

    @Override
    protected void configure() {
        //TODO find out what exactly tagging the owned skeletons with this does
        this.getOrCreateTagBuilder(EntityTypeTags.SKELETONS).add(OverlordEntities.OWNED_SKELETON_TYPE);
    }

    @Override
    protected void setContainer(TagContainer<EntityType<?>> tagContainer) {

    }

    @Override
    protected Path getOutput(Identifier identifier) {
        return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/entity_types/" + identifier.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Overlord Entity Types";
    }
}
