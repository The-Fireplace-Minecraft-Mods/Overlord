package dev.the_fireplace.overlord.tags;

import dev.the_fireplace.overlord.Overlord;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class OverlordBlockTags {
    public static Tag<Block> CASKETS = build("caskets");
    public static Tag<Block> GRAVE_MARKERS = build("grave_markers");
    //Anything that can be turned into Blood-Soaked Soil
    public static Tag<Block> DIRT = build("dirt");

    private static Tag<Block> build(String name) {
        return new Tag.Builder<Block>().build(new Identifier(Overlord.MODID, name));
    }
}
