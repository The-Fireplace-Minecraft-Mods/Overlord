package dev.the_fireplace.overlord.datagen;

import dev.the_fireplace.overlord.block.OverlordBlockTags;
import dev.the_fireplace.overlord.item.OverlordItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ItemTagsProvider extends FabricTagProvider.ItemTagProvider
{
    public ItemTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        this.copy(OverlordBlockTags.CASKETS, OverlordItemTags.CASKETS);
        this.copy(OverlordBlockTags.GRAVE_MARKERS, OverlordItemTags.GRAVE_MARKERS);
        this.getOrCreateTagBuilder(OverlordItemTags.MUSCLE_MEAT).add(
            Items.BEEF,
            Items.RABBIT,
            Items.MUTTON,
            Items.PORKCHOP,
            Items.CHICKEN
        );
        this.getOrCreateTagBuilder(OverlordItemTags.FLESH).add(
            Items.LEATHER,
            Items.PHANTOM_MEMBRANE
        );
        this.getOrCreateTagBuilder(OverlordItemTags.BONES).add(
            Items.BONE
        );
        this.getOrCreateTagBuilder(OverlordItemTags.DYES).add(
            Items.WHITE_DYE,
            Items.ORANGE_DYE,
            Items.MAGENTA_DYE,
            Items.LIGHT_BLUE_DYE,
            Items.YELLOW_DYE,
            Items.LIME_DYE,
            Items.PINK_DYE,
            Items.GRAY_DYE,
            Items.LIGHT_GRAY_DYE,
            Items.CYAN_DYE,
            Items.PURPLE_DYE,
            Items.BLUE_DYE,
            Items.BROWN_DYE,
            Items.GREEN_DYE,
            Items.RED_DYE,
            Items.BLACK_DYE
        );
    }
}
