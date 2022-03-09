package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.Overlord;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class OverlordBlockTags
{
    public static TagKey<Block> CASKETS = build("caskets");
    public static TagKey<Block> GRAVE_MARKERS = build("grave_markers");

    private static TagKey<Block> build(String name) {
        return TagKey.of(Registry.BLOCK_KEY, new Identifier(Overlord.MODID, name));
    }
}
