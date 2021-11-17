package dev.the_fireplace.overlord.block;

import dev.the_fireplace.overlord.Overlord;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class OverlordBlockTags
{
    public static Tag.Identified<Block> CASKETS = build("caskets");
    public static Tag.Identified<Block> GRAVE_MARKERS = build("grave_markers");

    private static Tag.Identified<Block> build(String name) {
        return (Tag.Identified<Block>) TagRegistry.block(new Identifier(Overlord.MODID, name));
    }
}
