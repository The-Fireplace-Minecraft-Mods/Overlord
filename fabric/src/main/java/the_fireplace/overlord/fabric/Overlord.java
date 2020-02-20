package the_fireplace.overlord.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.data.DataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import the_fireplace.overlord.fabric.init.OverlordBlocks;
import the_fireplace.overlord.fabric.init.OverlordItems;
import the_fireplace.overlord.fabric.init.datagen.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class Overlord implements ModInitializer {
    public static final String MODID = "overlord";

    public static final Logger LOGGER = LogManager.getLogger("overlord");

    @Override
    public void onInitialize() {
        LOGGER.debug("Preparing bones...");
        OverlordBlocks.registerBlocks();
        OverlordItems.registerItems();
        //noinspection ConstantConditions//TODO Use environment variables for this
        if(true) {
            LOGGER.debug("Generating data...");
            DataGenerator gen = new DataGenerator(Paths.get("common", "src", "main", "resources"), Collections.emptySet());
            gen.install(new BlockTagsProvider(gen));
            gen.install(new EntityTypeTagsProvider(gen));
            gen.install(new ItemTagsProvider(gen));
            gen.install(new RecipesProvider(gen));
            gen.install(new LootTablesProvider(gen));
            try {
                gen.run();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
