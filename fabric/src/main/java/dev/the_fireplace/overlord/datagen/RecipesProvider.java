package dev.the_fireplace.overlord.datagen;

import com.google.inject.Injector;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.item.OverlordItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

public class RecipesProvider extends FabricRecipeProvider
{

    //Workaround because the tag is empty for some reason. TODO convert to use the tag. Maybe using a command when the world is running will let it work?
    private final Ingredient bedIngredient = Ingredient.of(
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

    public RecipesProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateRecipes(Consumer<FinishedRecipe> consumer) {
        Injector injector = OverlordConstants.getInjector();
        OverlordItems overlordItems = injector.getInstance(OverlordItems.class);
        OverlordBlocks overlordBlocks = injector.getInstance(OverlordBlocks.class);
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.SMOOTH_STONE), overlordBlocks.getStoneTombstone()).unlockedBy("has_smooth_stone", this.conditionsFromItem(Blocks.SMOOTH_STONE)).save(consumer, OverlordConstants.MODID + ":stone_tombstone_from_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_ANDESITE), overlordBlocks.getAndesiteTombstone()).unlockedBy("has_polished_andesite", this.conditionsFromItem(Blocks.POLISHED_ANDESITE)).save(consumer, OverlordConstants.MODID + ":andesite_tombstone_from_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_DIORITE), overlordBlocks.getDioriteTombstone()).unlockedBy("has_polished_diorite", this.conditionsFromItem(Blocks.POLISHED_DIORITE)).save(consumer, OverlordConstants.MODID + ":diorite_tombstone_from_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_GRANITE), overlordBlocks.getGraniteTombstone()).unlockedBy("has_polished_granite", this.conditionsFromItem(Blocks.POLISHED_GRANITE)).save(consumer, OverlordConstants.MODID + ":granite_tombstone_from_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_BLACKSTONE), overlordBlocks.getBlackstoneTombstone()).unlockedBy("has_polished_blackstone", this.conditionsFromItem(Blocks.POLISHED_BLACKSTONE)).save(consumer, OverlordConstants.MODID + ":blackstone_tombstone_from_stonecutting");
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(Blocks.POLISHED_DEEPSLATE), overlordBlocks.getDeepslateTombstone()).unlockedBy("has_polished_deepslate", this.conditionsFromItem(Blocks.POLISHED_DEEPSLATE)).save(consumer, OverlordConstants.MODID + ":deepslate_tombstone_from_stonecutting");
        ShapedRecipeBuilder.shaped(overlordBlocks.getAcaciaCasket(), 1).define('#', Items.STRIPPED_ACACIA_WOOD).define('%', Items.ACACIA_PLANKS).define('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").unlockedBy("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_ACACIA_WOOD)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getBirchCasket(), 1).define('#', Items.STRIPPED_BIRCH_WOOD).define('%', Items.BIRCH_PLANKS).define('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").unlockedBy("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_BIRCH_WOOD)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getSpruceCasket(), 1).define('#', Items.STRIPPED_SPRUCE_WOOD).define('%', Items.SPRUCE_PLANKS).define('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").unlockedBy("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_SPRUCE_WOOD)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getJungleCasket(), 1).define('#', Items.STRIPPED_JUNGLE_WOOD).define('%', Items.JUNGLE_PLANKS).define('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").unlockedBy("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_JUNGLE_WOOD)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getOakCasket(), 1).define('#', Items.STRIPPED_OAK_WOOD).define('%', Items.OAK_PLANKS).define('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").unlockedBy("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_OAK_WOOD)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getDarkOakCasket(), 1).define('#', Items.STRIPPED_DARK_OAK_WOOD).define('%', Items.DARK_OAK_PLANKS).define('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").unlockedBy("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_DARK_OAK_WOOD)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getCrimsonCasket(), 1).define('#', Items.STRIPPED_CRIMSON_HYPHAE).define('%', Items.CRIMSON_PLANKS).define('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").unlockedBy("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_CRIMSON_HYPHAE)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getWarpedCasket(), 1).define('#', Items.STRIPPED_WARPED_HYPHAE).define('%', Items.WARPED_PLANKS).define('B', bedIngredient).pattern("###").pattern("%B%").pattern("###").group("wooden_casket").unlockedBy("has_stripped_wood", this.conditionsFromItem(Blocks.STRIPPED_WARPED_HYPHAE)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getAcaciaGraveMarker(), 2).define('#', Items.ACACIA_FENCE).define('%', Items.ACACIA_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").unlockedBy("has_fence", this.conditionsFromItem(Items.ACACIA_FENCE)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getBirchGraveMarker(), 2).define('#', Items.BIRCH_FENCE).define('%', Items.BIRCH_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").unlockedBy("has_fence", this.conditionsFromItem(Items.BIRCH_FENCE)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getSpruceGraveMarker(), 2).define('#', Items.SPRUCE_FENCE).define('%', Items.SPRUCE_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").unlockedBy("has_fence", this.conditionsFromItem(Items.SPRUCE_FENCE)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getJungleGraveMarker(), 2).define('#', Items.JUNGLE_FENCE).define('%', Items.JUNGLE_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").unlockedBy("has_fence", this.conditionsFromItem(Items.JUNGLE_FENCE)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getOakGraveMarker(), 2).define('#', Items.OAK_FENCE).define('%', Items.OAK_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").unlockedBy("has_fence", this.conditionsFromItem(Items.OAK_FENCE)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getDarkOakGraveMarker(), 2).define('#', Items.DARK_OAK_FENCE).define('%', Items.DARK_OAK_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").unlockedBy("has_fence", this.conditionsFromItem(Items.DARK_OAK_FENCE)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getCrimsonGraveMarker(), 2).define('#', Items.CRIMSON_FENCE).define('%', Items.CRIMSON_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").unlockedBy("has_fence", this.conditionsFromItem(Items.CRIMSON_FENCE)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordBlocks.getWarpedGraveMarker(), 2).define('#', Items.WARPED_FENCE).define('%', Items.WARPED_SLAB).pattern("%%%").pattern(" # ").pattern(" # ").group("grave_marker").unlockedBy("has_fence", this.conditionsFromItem(Items.WARPED_FENCE)).save(consumer);
        ShapedRecipeBuilder.shaped(overlordItems.getOrdersWand(), 1).define('#', Items.AMETHYST_SHARD).define('/', Items.STICK).pattern("  #").pattern(" / ").pattern("/  ").unlockedBy("has_amethyst_shard", this.conditionsFromItem(Items.AMETHYST_SHARD)).save(consumer);
    }

    @Override
    public String getName() {
        return "Overlord Recipes";
    }


    private InventoryChangeTrigger.TriggerInstance conditionsFromItem(ItemLike itemConvertible) {
        return this.conditionsFromItemPredicates(ItemPredicate.Builder.item().of(itemConvertible).build());
    }

    private InventoryChangeTrigger.TriggerInstance conditionsFromItemPredicates(ItemPredicate... items) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, items);
    }
}
