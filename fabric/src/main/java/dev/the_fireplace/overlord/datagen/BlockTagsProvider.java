package dev.the_fireplace.overlord.datagen;

import com.google.common.collect.Streams;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.OverlordBlockTags;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class BlockTagsProvider extends FabricTagProvider.BlockTagProvider
{
    public BlockTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
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
            overlordBlocks.getMangroveCasket(),
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
            overlordBlocks.getMangroveGraveMarker(),
        };
        Block[] stoneTombstones = {
            overlordBlocks.getStoneTombstone(),
            overlordBlocks.getDioriteTombstone(),
            overlordBlocks.getGraniteTombstone(),
            overlordBlocks.getAndesiteTombstone(),
            overlordBlocks.getBlackstoneTombstone(),
            overlordBlocks.getDeepslateTombstone(),
        };
        this.getOrCreateTagBuilder(OverlordBlockTags.CASKETS).add(
            woodCaskets
        );
        this.getOrCreateTagBuilder(OverlordBlockTags.GRAVE_MARKERS).add(
            woodGraveMarkers
        );
        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_SHOVEL).add(
            overlordBlocks.getBloodSoakedSoil()
        );
        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE).add(
            Streams.concat(
                Arrays.stream(woodCaskets),
                Arrays.stream(woodGraveMarkers)
            ).toArray(Block[]::new)
        );
        this.getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(
            stoneTombstones
        );
    }
}
