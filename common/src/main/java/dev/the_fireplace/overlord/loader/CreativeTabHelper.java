package dev.the_fireplace.overlord.loader;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

public interface CreativeTabHelper
{
    void registerItemAfter(CreativeModeTab tab, ItemLike afterLast, ItemLike... items);
}
