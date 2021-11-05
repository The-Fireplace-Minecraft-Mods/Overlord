package dev.the_fireplace.overlord.init.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.init.OverlordBlocks;
import net.minecraft.advancement.criterion.EnterBlockCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.SingleItemRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
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
public class RecipesProvider implements DataProvider
{
    private static final Logger LOGGER = LogManager.getLogger("Overlord Recipe Generator");
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator root;

    //Workaround because the tag is empty for some reason. TODO convert to use the tag. Maybe using a command when the world is running will let it work?
    private final Ingredient bedIngredient = Ingredient.ofItems(
        Items.BLACK_BED,
        Items.RED_BED,
        Items.BLUE_BED,
        Items.GREEN_BED,
        Items.YELLOW_BED,
        Items.PINK_BED,
        Items.CYAN_BED,
        Items.BROWN_BED,
        Items.GRAY_BED,
        Items.LIGHT_BLUE_BED,
        Items.LIGHT_GRAY_BED,
        Items.LIME_BED,
        Items.MAGENTA_BED,
        Items.ORANGE_BED,
        Items.PURPLE_BED,
        Items.WHITE_BED
    );

    public RecipesProvider(DataGenerator dataGenerator) {
        this.root = dataGenerator;
    }

    private void generate(Consumer<RecipeJsonProvider> consumer) {
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.SMOOTH_STONE), OverlordBlocks.STONE_TOMBSTONE).create("has_smooth_stone", this.conditionsFromItem(Blocks.SMOOTH_STONE)).offerTo(consumer, Overlord.MODID + ":stone_tombstone_from_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_ANDESITE), OverlordBlocks.ANDESITE_TOMBSTONE).create("has_polished_andesite", this.conditionsFromItem(Blocks.POLISHED_ANDESITE)).offerTo(consumer, Overlord.MODID + ":andesite_tombstone_from_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_DIORITE), OverlordBlocks.DIORITE_TOMBSTONE).create("has_polished_diorite", this.conditionsFromItem(Blocks.POLISHED_DIORITE)).offerTo(consumer, Overlord.MODID + ":diorite_tombstone_from_stonecutting");
        SingleItemRecipeJsonFactory.create(Ingredient.ofItems(Blocks.POLISHED_GRANITE), OverlordBlocks.GRANITE_TOMBSTONE).create("has_polished_granite", this.conditionsFromItem(Blocks.POLISHED_GRANITE)).offerTo(consumer, Overlord.MODID + ":granite_tombstone_from_stonecutting");
        //TODO why is the bed item tag empty?
        ShapedRecipeJsonFactory.create(OverlordBlocks.ACACIA_CASKET, 1).input('#', Items.STRIPPED_ACACIA_WOOD).input('%', Items.ACACIA_PLANKS).input('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").criterion("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_ACACIA_WOOD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.BIRCH_CASKET, 1).input('#', Items.STRIPPED_BIRCH_WOOD).input('%', Items.BIRCH_PLANKS).input('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").criterion("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_BIRCH_WOOD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.SPRUCE_CASKET, 1).input('#', Items.STRIPPED_SPRUCE_WOOD).input('%', Items.SPRUCE_PLANKS).input('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").criterion("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_SPRUCE_WOOD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.JUNGLE_CASKET, 1).input('#', Items.STRIPPED_JUNGLE_WOOD).input('%', Items.JUNGLE_PLANKS).input('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").criterion("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_JUNGLE_WOOD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.OAK_CASKET, 1).input('#', Items.STRIPPED_OAK_WOOD).input('%', Items.OAK_PLANKS).input('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").criterion("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_OAK_WOOD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.DARK_OAK_CASKET, 1).input('#', Items.STRIPPED_DARK_OAK_WOOD).input('%', Items.DARK_OAK_PLANKS).input('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").criterion("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_DARK_OAK_WOOD)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.ACACIA_GRAVE_MARKER, 2).input('#', Items.ACACIA_FENCE).input('%', Items.ACACIA_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").criterion("has_fence", this.conditionsFromItem(Items.ACACIA_FENCE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.BIRCH_GRAVE_MARKER, 2).input('#', Items.BIRCH_FENCE).input('%', Items.BIRCH_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").criterion("has_fence", this.conditionsFromItem(Items.BIRCH_FENCE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.SPRUCE_GRAVE_MARKER, 2).input('#', Items.SPRUCE_FENCE).input('%', Items.SPRUCE_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").criterion("has_fence", this.conditionsFromItem(Items.SPRUCE_FENCE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.JUNGLE_GRAVE_MARKER, 2).input('#', Items.JUNGLE_FENCE).input('%', Items.JUNGLE_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").criterion("has_fence", this.conditionsFromItem(Items.JUNGLE_FENCE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.OAK_GRAVE_MARKER, 2).input('#', Items.OAK_FENCE).input('%', Items.OAK_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").criterion("has_fence", this.conditionsFromItem(Items.OAK_FENCE)).offerTo(consumer);
        ShapedRecipeJsonFactory.create(OverlordBlocks.DARK_OAK_GRAVE_MARKER, 2).input('#', Items.DARK_OAK_FENCE).input('%', Items.DARK_OAK_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").criterion("has_fence", this.conditionsFromItem(Items.DARK_OAK_FENCE)).offerTo(consumer);
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
