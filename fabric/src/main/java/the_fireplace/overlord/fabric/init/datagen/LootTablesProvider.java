package the_fireplace.overlord.fabric.init.datagen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import the_fireplace.overlord.fabric.Overlord;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LootTablesProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger("Overlord Loot Tables Generator");
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator root;
    private final List<Pair<Supplier<Consumer<BiConsumer<Identifier, LootTable.Builder>>>, LootContextType>> lootTypeGenerators;

    public LootTablesProvider(DataGenerator dataGenerator) {
        this.lootTypeGenerators = ImmutableList.of(Pair.of(BlockLootTableGenerator::new, LootContextTypes.BLOCK));
        this.root = dataGenerator;
    }

    public void run(DataCache dataCache) {
        Path path = this.root.getOutput();
        Map<Identifier, LootTable> map = Maps.newHashMap();
        this.lootTypeGenerators.forEach((pair) -> {
            pair.getFirst().get().accept((identifier, builder) -> {
                if (map.put(identifier, builder.withType(pair.getSecond()).create()) != null) {
                    throw new IllegalStateException("Duplicate loot table " + identifier);
                }
            });
        });
        LootContextType lootContext = LootContextTypes.GENERIC;
        Function<Identifier, LootCondition> nullFunc = (identifierx) -> null;
        //map.getClass();
        LootTableReporter lootTableReporter = new LootTableReporter(lootContext, nullFunc, map::get);
        Set<Identifier> set = Sets.difference(LootTables.getAll(), map.keySet());

        for (Identifier identifier : set) {
            lootTableReporter.report("Missing built-in table: " + identifier);
        }

        map.forEach((identifierx, lootTable) -> {
            LootManager.check(lootTableReporter, identifierx, lootTable);
        });
        Multimap<String, String> multimap = lootTableReporter.getMessages();
        if (!multimap.isEmpty()) {
            multimap.forEach((string, string2) -> {
                LOGGER.warn("Found validation problem in " + string + ": " + string2);
            });
            Overlord.LOGGER.error("Failed to validate loot tables, see logs");
        } else {
            map.forEach((identifierx, lootTable) -> {
                Path path2 = getOutput(path, identifierx);

                try {
                    DataProvider.writeToPath(GSON, dataCache, LootManager.toJson(lootTable), path2);
                } catch (IOException e) {
                    LOGGER.error("Couldn't save loot table {}", path2, e);
                }

            });
        }
    }

    private static Path getOutput(Path rootOutput, Identifier lootTableId) {
        return rootOutput.resolve("data/" + lootTableId.getNamespace() + "/loot_tables/" + lootTableId.getPath() + ".json");
    }

    public String getName() {
        return "Overlord LootTables";
    }
}
