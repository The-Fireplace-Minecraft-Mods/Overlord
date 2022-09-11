package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

@Implementation
public final class FabricTagHelper implements TagHelper
{
    @Override
    public Tag.Named<Item> createItemTag(ResourceLocation identifier) {
        return TagFactory.ITEM.create(identifier);
    }

    @Override
    public Tag.Named<Block> createBlockTag(ResourceLocation identifier) {
        return TagFactory.BLOCK.create(identifier);
    }
}
