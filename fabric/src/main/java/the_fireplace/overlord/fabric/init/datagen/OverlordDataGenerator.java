package the_fireplace.overlord.fabric.init.datagen;

import net.minecraft.data.DataGenerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class OverlordDataGenerator {
    public static void main(String[] args) {
        DataGenerator gen = new DataGenerator(Paths.get("generated"), Collections.emptySet());
        gen.install(new BlockTagsProvider(gen));
        gen.install(new EntityTypeTagsProvider(gen));
        gen.install(new ItemTagsProvider(gen));
        gen.install(new RecipesProvider(gen));
        try {
            gen.run();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
