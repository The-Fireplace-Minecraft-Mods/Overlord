package dev.the_fireplace.overlord.datagen;

import com.google.common.collect.Streams;
import dev.the_fireplace.overlord.block.OverlordBlockTags;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.nio.file.Path;
import java.util.Arrays;

public class BlockTagsProvider extends AbstractTagProvider<Block> {
    public BlockTagsProvider(DataGenerator root) {
        super(root, Registry.BLOCK);
    }

    @Override
    protected void configure() {
        Block[] woodCaskets = {
            OverlordBlocks.OAK_CASKET,
            OverlordBlocks.BIRCH_CASKET,
            OverlordBlocks.JUNGLE_CASKET,
            OverlordBlocks.SPRUCE_CASKET,
            OverlordBlocks.ACACIA_CASKET,
            OverlordBlocks.DARK_OAK_CASKET,
            OverlordBlocks.WARPED_CASKET,
            OverlordBlocks.CRIMSON_CASKET,
        };
        Block[] woodGraveMarkers = {
            OverlordBlocks.OAK_GRAVE_MARKER,
            OverlordBlocks.BIRCH_GRAVE_MARKER,
            OverlordBlocks.JUNGLE_GRAVE_MARKER,
            OverlordBlocks.SPRUCE_GRAVE_MARKER,
            OverlordBlocks.ACACIA_GRAVE_MARKER,
            OverlordBlocks.DARK_OAK_GRAVE_MARKER,
            OverlordBlocks.WARPED_GRAVE_MARKER,
            OverlordBlocks.CRIMSON_GRAVE_MARKER,
        };
        Block[] stoneTombstones = {
            OverlordBlocks.STONE_TOMBSTONE,
            OverlordBlocks.DIORITE_TOMBSTONE,
            OverlordBlocks.GRANITE_TOMBSTONE,
            OverlordBlocks.ANDESITE_TOMBSTONE,
            OverlordBlocks.BLACKSTONE_TOMBSTONE,
            OverlordBlocks.DEEPSLATE_TOMBSTONE,
        };
        this.getOrCreateTagBuilder(OverlordBlockTags.CASKETS).add(
            woodCaskets
        );
        this.getOrCreateTagBuilder(OverlordBlockTags.GRAVE_MARKERS).add(
            woodGraveMarkers
        );
        this.getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE).add(
            OverlordBlocks.BLOOD_SOAKED_SOIL
        );
        this.getOrCreateTagBuilder(BlockTags.AXE_MINEABLE).add(
            Streams.concat(
                Arrays.stream(woodCaskets),
                Arrays.stream(woodGraveMarkers)
            ).toArray(Block[]::new)
        );
        this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(
            stoneTombstones
        );
    }

    @Override
    protected Path getOutput(Identifier identifier) {
        return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/blocks/" + identifier.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Overlord Block Tags";
    }

    @Override
    public Tag.Builder getTagBuilder(TagKey<Block> tag) {
        return super.getTagBuilder(tag);
    }
}
