package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.loader.TagHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public final class OverlordBlockTags
{
    public static Tag.Named<Block> CASKETS = build("caskets");
    public static Tag.Named<Block> GRAVE_MARKERS = build("grave_markers");

    private static Tag.Named<Block> build(String name) {
        TagHelper tagHelper = OverlordConstants.getInjector().getInstance(TagHelper.class);
        return tagHelper.createBlockTag(new ResourceLocation(OverlordConstants.MODID, name));
    }
}
