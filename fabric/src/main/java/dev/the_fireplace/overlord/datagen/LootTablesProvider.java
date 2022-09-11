package dev.the_fireplace.overlord.datagen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LootTablesProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger("Overlord Loot Tables Generator");
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator root;
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> lootTypeGenerators;

    public LootTablesProvider(DataGenerator dataGenerator) {
        this.lootTypeGenerators = ImmutableList.of(Pair.of(BlockLootTableGenerator::new, LootContextParamSets.BLOCK));
        this.root = dataGenerator;
    }

    public void run(HashCache dataCache) {
        Path path = this.root.getOutputFolder();
        Map<ResourceLocation, LootTable> map = Maps.newHashMap();
        this.lootTypeGenerators.forEach((pair) -> pair.getFirst().get().accept((identifier, builder) -> {
            if (map.put(identifier, builder.setParamSet(pair.getSecond()).build()) != null) {
                throw new IllegalStateException("Duplicate loot table " + identifier);
            }
        }));
        LootContextParamSet lootContext = LootContextParamSets.ALL_PARAMS;
        Function<ResourceLocation, LootItemCondition> nullFunc = (identifierx) -> null;
        //map.getClass();
        ValidationContext lootTableReporter = new ValidationContext(lootContext, nullFunc, map::get);

        map.forEach((identifierx, lootTable) -> LootTables.validate(lootTableReporter, identifierx, lootTable));
        Multimap<String, String> multimap = lootTableReporter.getProblems();
        if (!multimap.isEmpty()) {
            multimap.forEach((string, string2) -> LOGGER.warn("Found validation problem in " + string + ": " + string2));
            OverlordConstants.getLogger().error("Failed to validate loot tables, see logs");
        } else {
            map.forEach((identifierx, lootTable) -> {
                Path path2 = getOutput(path, identifierx);

                try {
                    DataProvider.save(GSON, dataCache, LootTables.serialize(lootTable), path2);
                } catch (IOException e) {
                    LOGGER.error("Couldn't save loot table {}", path2, e);
                }
            });
        }
    }

    private static Path getOutput(Path rootOutput, ResourceLocation lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/loot_tables/" + lootTableId.getPath() + ".json");
    }

    public String getName() {
        return "Overlord LootTables";
    }
}
