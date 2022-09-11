package dev.the_fireplace.overlord.datagen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AdvancementsProvider implements DataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator root;
    private final List<Consumer<Consumer<Advancement>>> tabGenerators = ImmutableList.of(
        OverlordConstants.getInjector().getInstance(OverlordTabAdvancementGenerator.class)
    );

    public AdvancementsProvider(DataGenerator root) {
        this.root = root;
    }

    @Override
    public void run(HashCache cache) {
        Path path = this.root.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (advancement) -> {
            if (!set.add(advancement.getId())) {
                throw new IllegalStateException("Duplicate advancement " + advancement.getId());
            } else {
                Path path2 = getOutput(path, advancement);

                try {
                    DataProvider.save(GSON, cache, advancement.deconstruct().serializeToJson(), path2);
                } catch (IOException var6) {
                    LOGGER.error("Couldn't save advancement {}", path2, var6);
                }

            }
        };

        for (Consumer<Consumer<Advancement>> tabGenerator : this.tabGenerators) {
            tabGenerator.accept(consumer);
        }
    }

    private static Path getOutput(Path rootOutput, Advancement advancement) {
        String namespace = advancement.getId().getNamespace();
        String path = advancement.getId().getPath();
        return rootOutput.resolve("data/" + namespace + "/advancements/" + path + ".json");
    }

    @Override
    public String getName() {
        return "Overlord Advancements";
    }
}
