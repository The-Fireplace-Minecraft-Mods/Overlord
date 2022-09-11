package dev.the_fireplace.overlord.loader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface TagHelper
{
    Tag.Named<Item> createItemTag(ResourceLocation identifier);

    Tag.Named<Block> createBlockTag(ResourceLocation identifier);
}
