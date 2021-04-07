package dev.the_fireplace.overlord.init.datagen;

import dev.the_fireplace.overlord.init.OverlordBlocks;
import dev.the_fireplace.overlord.tags.OverlordBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.nio.file.Path;

public class BlockTagsProvider extends AbstractTagProvider<Block> {
    public BlockTagsProvider(DataGenerator root) {
        super(root, Registry.BLOCK);
    }

    @Override
    protected void configure() {
        this.getOrCreateTagBuilder(OverlordBlockTags.CASKETS).add(OverlordBlocks.OAK_CASKET);
        this.getOrCreateTagBuilder(OverlordBlockTags.GRAVE_MARKERS).add(OverlordBlocks.OAK_GRAVE_MARKER);
        this.getOrCreateTagBuilder(OverlordBlockTags.DIRT).add(Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM);
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
