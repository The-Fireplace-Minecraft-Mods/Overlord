package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.Overlord;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class OverlordBlockTags
{
    public static Tag<Block> CASKETS = build("caskets");
    public static Tag<Block> GRAVE_MARKERS = build("grave_markers");

    private static Tag<Block> build(String name) {
        return TagRegistry.block(new Identifier(Overlord.MODID, name));
    }
}
