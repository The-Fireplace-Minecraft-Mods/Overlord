package dev.the_fireplace.overlord.datapack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.the_fireplace.lib.api.io.injectables.JsonFileReader;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonIngredient;
import dev.the_fireplace.overlord.entity.creation.SkeletonComponent;
import dev.the_fireplace.overlord.entity.creation.SkeletonRecipe;
import dev.the_fireplace.overlord.entity.creation.SkeletonRecipeRegistryImpl;
import dev.the_fireplace.overlord.entity.creation.ingredient.JsonIngredient;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.*;

public class SkeletonBuildingReloadListener implements ResourceManagerReloadListener
{
    private final SkeletonRecipeRegistryImpl skeletonRecipeRegistry;
    private final JsonFileReader jsonFileReader;

    @Inject
    public SkeletonBuildingReloadListener(SkeletonRecipeRegistryImpl skeletonRecipeRegistry, JsonFileReader jsonFileReader) {
        this.skeletonRecipeRegistry = skeletonRecipeRegistry;
        this.jsonFileReader = jsonFileReader;
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        Map<String, JsonObject> recipeJsons = new HashMap<>();
        String lastStandardRecipeDomain = "";
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources("skeleton_recipes", path -> path.getPath().endsWith(".json")).entrySet()) {
            JsonObject jsonObject = null;
            try (InputStream stream = entry.getValue().open()) {
                jsonObject = jsonFileReader.readJsonFromStream(stream);
            } catch (Exception e) {
                OverlordConstants.getLogger().error("Error occurred while loading resource json " + entry.toString(), e);
            }
            if (jsonObject == null) {
                continue;
            }
            String resourceDomain = entry.getKey().getNamespace();
            String resourcePath = entry.getKey().getPath();
            boolean isStandardRecipe = resourcePath.equals("skeleton_recipes/standard.json");
            if (!recipeJsons.containsKey(resourcePath)) {
                recipeJsons.put(resourcePath, jsonObject);
                if (isStandardRecipe) {
                    lastStandardRecipeDomain = resourceDomain;
                }
            } else {
                JsonObject existing = recipeJsons.get(resourcePath);
                int newPriority = jsonObject.has("priority") ? jsonObject.get("priority").getAsInt() : 0;
                if (!existing.has("priority") || existing.get("priority").getAsInt() <= newPriority) {
                    recipeJsons.put(resourcePath, jsonObject);
                    if (isStandardRecipe) {
                        lastStandardRecipeDomain = resourceDomain;
                    }
                }
            }
        }
        boolean hasDefaultRecipe = lastStandardRecipeDomain.equals(OverlordConstants.MODID);
        if (lastStandardRecipeDomain.isEmpty()) {
            OverlordConstants.getLogger().error("No standard recipe found.");
        } else if (!hasDefaultRecipe) {
            OverlordConstants.getLogger().info("The default skeleton recipe is overridden by " + lastStandardRecipeDomain);
        } else {
            OverlordConstants.getLogger().info("The default skeleton recipe is loaded.");
        }
        addRecipes(recipeJsons.values());
    }

    private void addRecipes(Collection<JsonObject> recipeJsons) {
        Collection<SkeletonRecipe> recipes = new HashSet<>();
        for (JsonObject recipeJson : recipeJsons) {
            if (!recipeJson.has("essential")) {
                OverlordConstants.getLogger().error("Data pack skeleton recipe is missing the essential section, skipping.");
                continue;
            }
            SkeletonComponent essentialComponent = getComponent(recipeJson.getAsJsonObject("essential"), 20);
            SkeletonComponent musclesComponent = recipeJson.has("muscles")
                ? getComponent(recipeJson.getAsJsonObject("muscles"), 4)
                : new SkeletonComponent();
            SkeletonComponent skinComponent = recipeJson.has("skin")
                ? getComponent(recipeJson.getAsJsonObject("skin"), 2)
                : new SkeletonComponent();
            SkeletonComponent playerColorsComponent = recipeJson.has("player_colors")
                ? getComponent(recipeJson.getAsJsonObject("player_colors"), 0)
                : new SkeletonComponent();
            SkeletonRecipe recipe = new SkeletonRecipe(essentialComponent, musclesComponent, skinComponent, playerColorsComponent);
            recipes.add(recipe);
        }
        skeletonRecipeRegistry.setSkeletonRecipes(recipes);
    }

    private SkeletonComponent getComponent(JsonObject jsonObject, int defaultHealth) {
        SkeletonComponent component = new SkeletonComponent();
        JsonArray ingredientsJson = jsonObject.has("ingredients") ? jsonObject.getAsJsonArray("ingredients") : new JsonArray();
        Collection<SkeletonIngredient> ingredients = new HashSet<>();
        for (JsonElement element : ingredientsJson) {
            ingredients.add(JsonIngredient.parse(element.getAsJsonObject()));
        }
        component.setIngredients(ingredients);
        JsonArray byproductsJson = jsonObject.has("byproducts") ? jsonObject.getAsJsonArray("byproducts") : new JsonArray();
        component.setByproducts(readByproducts(byproductsJson));
        int health = jsonObject.has("health") ? jsonObject.get("health").getAsInt() : defaultHealth;
        component.setMaxHealth(health);
        return component;
    }

    private Collection<ItemStack> readByproducts(JsonArray byproducts) {
        Collection<ItemStack> byproductStacks = new HashSet<>();
        for (JsonElement byproductElement : byproducts) {
            JsonObject byproduct = byproductElement.getAsJsonObject();
            ResourceLocation byproductIdentifier = new ResourceLocation(byproduct.get("id").getAsString());
            Optional<Item> byproductItem = Registry.ITEM.getOptional(byproductIdentifier);
            if (byproductItem.isEmpty()) {
                OverlordConstants.getLogger().warn("Byproduct not found, skipping: {}", byproductIdentifier.toString());
                continue;
            }
            int byproductCount = byproduct.get("count").getAsInt();
            ItemStack byproductStack = new ItemStack(byproductItem.get(), byproductCount);
            if (byproduct.has("nbt")) {
                CompoundTag byproductNbt = JsonIngredient.parseNbt(byproduct);
                byproductStack.setTag(byproductNbt);
            }
            byproductStacks.add(byproductStack);
        }
        return byproductStacks;
    }
}
