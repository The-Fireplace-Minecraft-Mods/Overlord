package dev.the_fireplace.overlord.entrypoints;

import dev.the_fireplace.overlord.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class DataGenerator implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        BlockTagsProvider blockTagProvider = new BlockTagsProvider(generator);
        generator.addProvider(blockTagProvider);
        generator.addProvider(new EntityTypeTagsProvider(generator));
        generator.addProvider(new ItemTagsProvider(generator, blockTagProvider));
        generator.addProvider(new RecipesProvider(generator));
        generator.addProvider(new BlockLootTableGenerator(generator));
        generator.addProvider(new AdvancementsProvider(generator));
    }
}
