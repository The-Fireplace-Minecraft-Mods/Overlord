package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class OverlordBlockTags
{
    public static TagKey<Block> CASKETS = build("caskets");
    public static TagKey<Block> GRAVE_MARKERS = build("grave_markers");

    private static TagKey<Block> build(String name) {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(OverlordConstants.MODID, name));
    }
}
