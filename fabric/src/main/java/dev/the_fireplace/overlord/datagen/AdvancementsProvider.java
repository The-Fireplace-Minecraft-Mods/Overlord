package dev.the_fireplace.overlord.datagen;

import com.google.common.collect.ImmutableList;
import dev.the_fireplace.overlord.OverlordConstants;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;

import java.util.List;
import java.util.function.Consumer;

public class AdvancementsProvider extends FabricAdvancementProvider
{
    private final List<Consumer<Consumer<Advancement>>> tabGenerators = ImmutableList.of(
        OverlordConstants.getInjector().getInstance(OverlordTabAdvancementGenerator.class)
    );

    public AdvancementsProvider(FabricDataGenerator root) {
        super(root);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        for (Consumer<Consumer<Advancement>> tabGenerator : this.tabGenerators) {
            tabGenerator.accept(consumer);
        }
    }

    @Override
    public String getName() {
        return "Overlord Advancements";
    }
}
