package dev.the_fireplace.overlord.init.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.the_fireplace.overlord.init.OverlordBlocks;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class RecipesProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger("Overlord Recipe Generator");
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator root;

    public RecipesProvider(DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }

    private void generate(Consumer<RecipeJsonProvider> consumer) {
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_STONE), OverlordBlocks.STONE_TOMBSTONE).create("has_smooth_stone", this.conditionsFromItem(Blocks.SMOOTH_STONE)).offerTo(consumer, "stone_tombstone_from_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_ANDESITE), OverlordBlocks.ANDESITE_TOMBSTONE).create("has_polished_andesite", this.conditionsFromItem(Blocks.POLISHED_ANDESITE)).offerTo(consumer, "andesite_tombstone_from_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_DIORITE), OverlordBlocks.DIORITE_TOMBSTONE).create("has_polished_diorite", this.conditionsFromItem(Blocks.POLISHED_DIORITE)).offerTo(consumer, "diorite_tombstone_from_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_GRANITE), OverlordBlocks.GRANITE_TOMBSTONE).create("has_polished_granite", this.conditionsFromItem(Blocks.POLISHED_GRANITE)).offerTo(consumer, "granite_tombstone_from_stonecutting");
    }

    public String getName() {
        return "Overlord Recipes";
    }

    public void run(DataCache dataCache) {
        Path path = this.root.getOutput();
        Set<Identifier> set = Sets.newHashSet();
        this.generate((recipeJsonProvider) -> {
            if (!set.add(recipeJsonProvider.getRecipeId())) {
                throw new IllegalStateException("Duplicate recipe " + recipeJsonProvider.getRecipeId());
            } else {
                this.saveRecipe(dataCache, recipeJsonProvider.toJson(), path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/recipes/" + recipeJsonProvider.getRecipeId().getPath() + ".json"));
                JsonObject jsonObject = recipeJsonProvider.toAdvancementJson();
                if (jsonObject != null) {
                    this.saveRecipeAdvancement(dataCache, jsonObject, path.resolve("data/" + recipeJsonProvider.getRecipeId().getNamespace() + "/advancements/" + recipeJsonProvider.getAdvancementId().getPath() + ".json"));
                }

            }
        });
    }

    private void saveRecipe(DataCache dataCache, JsonObject jsonObject, Path path) {
        try {
            save(dataCache, jsonObject, path);
        } catch (IOException var19) {
            LOGGER.error("Couldn't save recipe {}", path, var19);
        }
    }

    private void saveRecipeAdvancement(DataCache dataCache, JsonObject jsonObject, Path path) {
        try {
            save(dataCache, jsonObject, path);
        } catch (IOException var19) {
            LOGGER.error("Couldn't save recipe advancement {}", path, var19);
        }
    }

    private void save(DataCache dataCache, JsonObject jsonObject, Path path) throws IOException {
        String string = GSON.toJson(jsonObject);
        String string2 = SHA1.hashUnencodedChars(string).toString();
        if (!Objects.equals(dataCache.getOldSha1(path), string2) || !Files.exists(path)) {
            Files.createDirectories(path.getParent());
            BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
            Throwable var7 = null;

            try {
                bufferedWriter.write(string);
            } catch (Throwable var17) {
                var7 = var17;
                throw var17;
            } finally {
                //noinspection ConstantConditions
                if (bufferedWriter != null) {
                    if (var7 != null) {
                        try {
                            bufferedWriter.close();
                        } catch (Throwable var16) {
                            var7.addSuppressed(var16);
                        }
                    } else {
                        bufferedWriter.close();
                    }
                }

            }
        }

        dataCache.updateSha1(path, string2);
    }

    private EnterBlockCriterion.Conditions requireEnteringFluid(Block block) {
        return new EnterBlockCriterion.Conditions(block, StatePredicate.ANY);
    }

    private InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible itemConvertible) {
        return this.conditionsFromItemPredicates(ItemPredicate.Builder.create().item(itemConvertible).build());
    }

    private InventoryChangedCriterion.Conditions conditionsFromTag(Tag<Item> tag) {
        return this.conditionsFromItemPredicates(ItemPredicate.Builder.create().tag(tag).build());
    }

    private InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate... items) {
        return new InventoryChangedCriterion.Conditions(NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, items);
    }
}
