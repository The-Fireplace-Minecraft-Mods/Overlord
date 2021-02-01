package the_fireplace.overlord.init.datagen;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
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
}
