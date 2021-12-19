package dev.the_fireplace.overlord.datagen;

import dev.the_fireplace.lib.api.datagen.injectables.DataGeneratorFactory;
import dev.the_fireplace.overlord.Overlord;
import net.minecraft.data.DataGenerator;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Paths;

public class OverlordDataGeneratorRunner
{
    private final DataGeneratorFactory dataGeneratorFactory;

    @Inject
    public OverlordDataGeneratorRunner(DataGeneratorFactory dataGeneratorFactory) {
        this.dataGeneratorFactory = dataGeneratorFactory;
    }

    public void run() {
        Overlord.getLogger().debug("Generating data...");
        DataGenerator generator = dataGeneratorFactory.createAdditive(Paths.get("..", "src", "main", "resources"));
        BlockTagsProvider blockTagsProvider = new BlockTagsProvider(generator);
        generator.addProvider(blockTagsProvider);
        generator.addProvider(new EntityTypeTagsProvider(generator));
        generator.addProvider(new ItemTagsProvider(generator, blockTagsProvider));
        generator.addProvider(new RecipesProvider(generator));
        generator.addProvider(new LootTablesProvider(generator));
        generator.addProvider(new AdvancementsProvider(generator));
        try {
            generator.run();
        } catch (IOException e) {
            Overlord.getLogger().error(e);
        }
    }
}
