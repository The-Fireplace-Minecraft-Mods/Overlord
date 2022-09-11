package dev.the_fireplace.overlord.datagen;

import dev.the_fireplace.overlord.block.OverlordBlockTags;
import dev.the_fireplace.overlord.item.OverlordItemTags;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

public class ItemTagsProvider extends TagsProvider<Item>
{
    private final Function<TagKey<Block>, Tag.Builder> blockTagBuilder;

    public ItemTagsProvider(DataGenerator root, BlockTagsProvider blockTagsProvider) {
        super(root, Registry.ITEM);
        this.blockTagBuilder = blockTagsProvider::getOrCreateRawBuilder;
    }

    @Override
    protected void addTags() {
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

    protected void copy(TagKey<Block> blockTag, TagKey<Item> itemTag) {
        Tag.Builder itemTagBuilder = this.getOrCreateRawBuilder(itemTag);
        Tag.Builder blockTagBuilder = this.blockTagBuilder.apply(blockTag);
        Objects.requireNonNull(itemTagBuilder);
        blockTagBuilder.getEntries().forEach(itemTagBuilder::add);
    }

    @Override
    protected Path getPath(ResourceLocation identifier) {
        return this.generator.getOutputFolder().resolve("data/" + identifier.getNamespace() + "/tags/items/" + identifier.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Overlord Item Tags";
    }
}
