package dev.the_fireplace.overlord.loader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface TagHelper
{
    Tag<Item> createItemTag(ResourceLocation identifier);

    Tag<Block> createBlockTag(ResourceLocation identifier);
}
