package dev.the_fireplace.overlord.loader;

import net.minecraft.world.level.ItemLike;

public interface CreativeTabHelper
{
    void registerItemAfter(ItemLike afterLast, ItemLike... items);
}
