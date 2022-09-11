package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

@Implementation
public final class ForgeTagHelper implements TagHelper
{
    @Override
    public Tag.Named<Item> createItemTag(ResourceLocation identifier) {
        return ItemTags.bind(identifier.toString());
    }

    @Override
    public Tag.Named<Block> createBlockTag(ResourceLocation identifier) {
        return BlockTags.bind(identifier.toString());
    }
}
