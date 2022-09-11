package dev.the_fireplace.overlord.entrypoints;

import dev.the_fireplace.overlord.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class DataGenerator implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        BlockTagsProvider blockTagsProvider = new BlockTagsProvider(generator);
        generator.addProvider(blockTagsProvider);
        generator.addProvider(new EntityTypeTagsProvider(generator));
        generator.addProvider(new ItemTagsProvider(generator, blockTagsProvider));
        generator.addProvider(new RecipesProvider(generator));
        generator.addProvider(new LootTablesProvider(generator));
        generator.addProvider(new AdvancementsProvider(generator));
    }
}
