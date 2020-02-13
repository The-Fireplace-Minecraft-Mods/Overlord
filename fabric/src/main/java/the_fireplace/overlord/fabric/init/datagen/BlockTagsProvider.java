package the_fireplace.overlord.fabric.init.datagen;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.fabric.init.OverlordBlocks;
import the_fireplace.overlord.fabric.tags.OverlordBlockTags;

import java.nio.file.Path;

public class BlockTagsProvider extends AbstractTagProvider<Block> {
    protected BlockTagsProvider(DataGenerator root) {
        super(root, Registry.BLOCK);
    }

    @Override
    protected void configure() {
        this.getOrCreateTagBuilder(OverlordBlockTags.CASKETS).add(OverlordBlocks.OAK_CASKET);
    }

    @Override
    protected void setContainer(TagContainer<Block> tagContainer) {

    }

    @Override
    protected Path getOutput(Identifier identifier) {
        return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/blocks/" + identifier.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Overlord Block Tags";
    }
}
