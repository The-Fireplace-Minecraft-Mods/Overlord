package dev.the_fireplace.overlord.datagen;

import dev.the_fireplace.overlord.block.OverlordBlockTags;
import dev.the_fireplace.overlord.item.OverlordItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.world.item.Items;

public class ItemTagsProvider extends FabricTagProvider.ItemTagProvider
{
    public ItemTagsProvider(FabricDataGenerator root, BlockTagProvider blockTagProvider) {
        super(root, blockTagProvider);
    }

    @Override
    protected void generateTags() {
        this.copy(OverlordBlockTags.CASKETS, OverlordItemTags.CASKETS);
        this.copy(OverlordBlockTags.GRAVE_MARKERS, OverlordItemTags.GRAVE_MARKERS);
        this.tag(OverlordItemTags.MUSCLE_MEAT).add(
            Items.BEEF,
            Items.RABBIT,
            Items.MUTTON,
            Items.PORKCHOP,
            Items.CHICKEN
        );
        this.tag(OverlordItemTags.FLESH).add(
            Items.LEATHER,
            Items.PHANTOM_MEMBRANE
        );
        this.tag(OverlordItemTags.BONES).add(
            Items.BONE
        );
        this.tag(OverlordItemTags.DYES).add(
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
