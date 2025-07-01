package dev.the_fireplace.overlord.entrypoints;

import dev.the_fireplace.overlord.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public final class DataGenerator implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        var blockTags = pack.addProvider(BlockTagsProvider::new);
        pack.addProvider(EntityTypeTagsProvider::new);
        pack.addProvider((FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) ->
            new ItemTagsProvider(output, completableFuture, blockTags)
        );
        pack.addProvider(RecipesProvider::new);
        pack.addProvider(BlockLootTableGenerator::new);
        pack.addProvider(AdvancementsProvider::new);
    }
}
