package dev.the_fireplace.overlord.init.datagen;

import dev.the_fireplace.overlord.tags.OverlordBlockTags;
import dev.the_fireplace.overlord.tags.OverlordItemTags;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.nio.file.Path;
import java.util.function.Function;

public class ItemTagsProvider extends AbstractTagProvider<Item>
{
    private final Function<Tag.Identified<Block>, Tag.Builder> field_23783;

    public ItemTagsProvider(DataGenerator root, BlockTagsProvider blockTagsProvider) {
        super(root, Registry.ITEM);
        this.field_23783 = blockTagsProvider::method_27169;
    }

    @Override
    protected void configure() {
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
    }

    protected void copy(Tag.Identified<Block> blockTag, Tag.Identified<Item> itemTag) {
        Tag.Builder builder = this.method_27169(itemTag);
        Tag.Builder builder2 = this.field_23783.apply(blockTag);
        builder2.streamEntries().forEach(builder::add);
    }

    @Override
    protected Path getOutput(Identifier identifier) {
        return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/items/" + identifier.getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Overlord Item Tags";
    }
}
