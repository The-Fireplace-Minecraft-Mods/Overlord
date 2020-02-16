package the_fireplace.overlord.fabric.init.datagen;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.AbstractTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import the_fireplace.overlord.fabric.Overlord;
import the_fireplace.overlord.fabric.tags.OverlordBlockTags;
import the_fireplace.overlord.fabric.tags.OverlordItemTags;

import java.nio.file.Path;
import java.util.List;

public class ItemTagsProvider extends AbstractTagProvider<Item> {
    public ItemTagsProvider(DataGenerator root) {
        super(root, Registry.ITEM);
    }

    @Override
    protected void configure() {
        this.copy(OverlordBlockTags.CASKETS, OverlordItemTags.CASKETS);
    }

    protected void copy(Tag<Block> tag, Tag<Item> tag2) {
        Tag.Builder<Item> builder = this.getOrCreateTagBuilder(tag2);

        for (Tag.Entry<Block> blockEntry : tag.entries()) {
            Tag.Entry<Item> entry2 = this.convert(blockEntry);
            builder.add(entry2);
        }
    }

    private Tag.Entry<Item> convert(Tag.Entry<Block> entry) {
        if (entry instanceof Tag.TagEntry) {
            return new Tag.TagEntry<>(((Tag.TagEntry<Block>)entry).getId());
        } else if (entry instanceof Tag.CollectionEntry) {
            List<Item> list = Lists.newArrayList();

            for (Object o : ((Tag.CollectionEntry<Block>) entry).getValues()) {
                Block block = (Block) o;
                Item item = block.asItem();
                if (item == Items.AIR) {
                    Overlord.LOGGER.warn("Itemless block copied to item tag: {}", Registry.BLOCK.getId(block));
                } else {
                    list.add(item);
                }
            }

            return new Tag.CollectionEntry<>(list);
        } else {
            throw new UnsupportedOperationException("Unknown tag entry " + entry);
        }
    }

    @Override
    protected void setContainer(TagContainer<Item> tagContainer) {

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
