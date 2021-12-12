package dev.the_fireplace.overlord.datapack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.the_fireplace.lib.api.io.injectables.JsonFileReader;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.entity.creation.SkeletonIngredient;
import dev.the_fireplace.overlord.entity.creation.SkeletonBuilderImpl;
import dev.the_fireplace.overlord.entity.creation.SkeletonComponent;
import dev.the_fireplace.overlord.entity.creation.SkeletonRecipe;
import dev.the_fireplace.overlord.entity.creation.ingredient.JsonIngredient;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.*;

public class SkeletonBuildingPackLoader implements SimpleSynchronousResourceReloadListener
{
    private final SkeletonBuilderImpl skeletonBuilder;
    private final JsonFileReader jsonFileReader;

    @Inject
    public SkeletonBuildingPackLoader(SkeletonBuilderImpl skeletonBuilder, JsonFileReader jsonFileReader) {
        this.skeletonBuilder = skeletonBuilder;
        this.jsonFileReader = jsonFileReader;
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Overlord.MODID, "skeleton_building");
    }

    @Override
    public void reload(ResourceManager manager) {
        Map<String, JsonObject> recipeJsons = new HashMap<>();
        for (Identifier id : manager.findResources("skeleton_recipes", path -> path.endsWith(".json"))) {
            JsonObject jsonObject = null;
            try (InputStream stream = manager.getResource(id).getInputStream()) {
                jsonObject = jsonFileReader.readJsonFromStream(stream);
            } catch (Exception e) {
                Overlord.getLogger().error("Error occurred while loading resource json " + id.toString(), e);
            }
            if (jsonObject == null) {
                continue;
            }
            String resourcePath = id.getPath();
            if (!recipeJsons.containsKey(resourcePath)) {
                recipeJsons.put(resourcePath, jsonObject);
            } else {
                JsonObject existing = recipeJsons.get(resourcePath);
                int newPriority = jsonObject.has("priority") ? jsonObject.get("priority").getAsInt() : 0;
                if (!existing.has("priority") || existing.get("priority").getAsInt() <= newPriority) {
                    recipeJsons.put(resourcePath, jsonObject);
                }
            }
        }
        addRecipes(recipeJsons.values());
    }

    private void addRecipes(Collection<JsonObject> recipeJsons) {
        Collection<SkeletonRecipe> recipes = new HashSet<>();
        for (JsonObject recipeJson : recipeJsons) {
            if (!recipeJson.has("essential")) {
                Overlord.getLogger().error("Data pack skeleton recipe is missing the essential section, skipping.");
                continue;
            }
            SkeletonComponent essentialComponent = getComponent(recipeJson.getAsJsonObject("essential"));
            SkeletonComponent musclesComponent = recipeJson.has("muscles") ? getComponent(recipeJson.getAsJsonObject("muscles")) : new SkeletonComponent();
            SkeletonComponent skinComponent = recipeJson.has("skin") ? getComponent(recipeJson.getAsJsonObject("skin")) : new SkeletonComponent();
            SkeletonComponent playerColorsComponent = recipeJson.has("player_colors") ? getComponent(recipeJson.getAsJsonObject("player_colors")) : new SkeletonComponent();
            SkeletonRecipe recipe = new SkeletonRecipe(essentialComponent, musclesComponent, skinComponent, playerColorsComponent);
            recipes.add(recipe);
        }
        skeletonBuilder.setSkeletonRecipes(recipes);
    }

    private SkeletonComponent getComponent(JsonObject jsonObject) {
        SkeletonComponent component = new SkeletonComponent();
        JsonArray ingredientsJson = jsonObject.has("ingredients") ? jsonObject.getAsJsonArray("ingredients") : new JsonArray();
        Collection<SkeletonIngredient> ingredients = new HashSet<>();
        for (JsonElement element : ingredientsJson) {
            ingredients.add(JsonIngredient.parse(element.getAsJsonObject()));
        }
        component.setIngredients(ingredients);
        JsonArray byproductsJson = jsonObject.has("byproducts") ? jsonObject.getAsJsonArray("byproducts") : new JsonArray();
        component.setByproducts(readByproducts(byproductsJson));
        return component;
    }

    private Collection<ItemStack> readByproducts(JsonArray byproducts) {
        Collection<ItemStack> byproductStacks = new HashSet<>();
        for (JsonElement byproductElement : byproducts) {
            JsonObject byproduct = byproductElement.getAsJsonObject();
            Identifier byproductIdentifier = new Identifier(byproduct.get("id").getAsString());
            Optional<Item> byproductItem = Registry.ITEM.getOrEmpty(byproductIdentifier);
            if (!byproductItem.isPresent()) {
                Overlord.getLogger().warn("Byproduct not found, skipping: {}", byproductIdentifier.toString());
                continue;
            }
            int byproductCount = byproduct.get("count").getAsInt();
            ItemStack byproductStack = new ItemStack(byproductItem.get(), byproductCount);
            if (byproduct.has("nbt")) {
                NbtCompound byproductNbt = JsonIngredient.parseNbt(byproduct);
                byproductStack.setTag(byproductNbt);
            }
            byproductStacks.add(byproductStack);
        }
        return byproductStacks;
    }
}
