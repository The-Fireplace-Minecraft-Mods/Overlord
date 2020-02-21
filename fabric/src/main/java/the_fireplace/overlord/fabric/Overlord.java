package the_fireplace.overlord.fabric;

import com.google.common.collect.Lists;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import the_fireplace.overlord.ILoaderHelper;
import the_fireplace.overlord.OverlordHelper;
import the_fireplace.overlord.fabric.init.OverlordBlocks;
import the_fireplace.overlord.fabric.init.OverlordItems;
import the_fireplace.overlord.fabric.init.datagen.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class Overlord implements ModInitializer, ILoaderHelper {
    public static final String MODID = "overlord";

    public static final Logger LOGGER = LogManager.getLogger("overlord");

    private static final List<String> mobIds = Lists.newArrayList();
    private static final List<String> animalIds = Lists.newArrayList();

    @Override
    public void onInitialize() {
        LOGGER.debug("Preparing bones...");
        OverlordHelper.setLoaderHelper(this);
        OverlordBlocks.registerBlocks();
        OverlordItems.registerItems();
        //noinspection ConstantConditions//TODO Use environment variables for this
        if(false) {
            LOGGER.debug("Generating data...");
            DataGenerator gen = new AdditiveDataGenerator(Paths.get("..", "..", "common", "src", "main", "resources"), Collections.emptySet());
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

        ServerStartCallback.EVENT.register(s -> {
            LOGGER.debug("Raising the dead...");
            for (EntityType<?> entityType : Registry.ENTITY_TYPE) {
                if (!entityType.getCategory().isPeaceful())
                    mobIds.add(Registry.ENTITY_TYPE.getId(entityType).toString());
                if (entityType.getCategory().isAnimal())
                    animalIds.add(Registry.ENTITY_TYPE.getId(entityType).toString());
            }
        });
    }

    @Override
    public List<String> getMobIds() {
        return mobIds;
    }

    @Override
    public List<String> getAnimalIds() {
        return animalIds;
    }
}
