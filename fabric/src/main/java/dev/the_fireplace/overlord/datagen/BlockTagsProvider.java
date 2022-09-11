package dev.the_fireplace.overlord.datagen;

import com.google.common.collect.Streams;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.OverlordBlockTags;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.Arrays;

public class BlockTagsProvider extends TagsProvider<Block>
{
    public BlockTagsProvider(DataGenerator root) {
        super(root, Registry.BLOCK);
    }

    @Override
    protected void addTags() {
        OverlordBlocks overlordBlocks = OverlordConstants.getInjector().getInstance(OverlordBlocks.class);
        Block[] woodCaskets = {
            overlordBlocks.getOakCasket(),
            overlordBlocks.getBirchCasket(),
            overlordBlocks.getSpruceCasket(),
            overlordBlocks.getJungleCasket(),
            overlordBlocks.getAcaciaCasket(),
            overlordBlocks.getDarkOakCasket(),
            overlordBlocks.getWarpedCasket(),
            overlordBlocks.getCrimsonCasket(),
        };
        Block[] woodGraveMarkers = {
            overlordBlocks.getOakGraveMarker(),
            overlordBlocks.getBirchGraveMarker(),
            overlordBlocks.getJungleGraveMarker(),
            overlordBlocks.getSpruceGraveMarker(),
            overlordBlocks.getAcaciaGraveMarker(),
            overlordBlocks.getDarkOakGraveMarker(),
            overlordBlocks.getWarpedGraveMarker(),
            overlordBlocks.getCrimsonGraveMarker(),
        };
        Block[] stoneTombstones = {
            overlordBlocks.getStoneTombstone(),
            overlordBlocks.getDioriteTombstone(),
            overlordBlocks.getGraniteTombstone(),
            overlordBlocks.getAndesiteTombstone(),
            overlordBlocks.getBlackstoneTombstone(),
            overlordBlocks.getDeepslateTombstone(),
        };
        this.tag(OverlordBlockTags.CASKETS).add(
            woodCaskets
        );
        this.tag(OverlordBlockTags.GRAVE_MARKERS).add(
            woodGraveMarkers
        );
        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
            overlordBlocks.getBloodSoakedSoil()
        );
        this.tag(BlockTags.MINEABLE_WITH_AXE).add(
            Streams.concat(
                Arrays.stream(woodCaskets),
                Arrays.stream(woodGraveMarkers)
            ).toArray(Block[]::new)
        );
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
            stoneTombstones
        );
    }

    @Override
    protected Path getPath(ResourceLocation identifier) {
        return this.generator.getOutputFolder().resolve("data/" + identifier.getNamespace() + "/tags/blocks/" + identifier.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Overlord Block Tags";
    }

    @Override
    public Tag.Builder getOrCreateRawBuilder(TagKey<Block> tag) {
        return super.getOrCreateRawBuilder(tag);
    }
}
