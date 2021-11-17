package dev.the_fireplace.overlord.tags;

import dev.the_fireplace.overlord.Overlord;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class OverlordBlockTags
{
    public static Tag.Identified<Block> CASKETS = build("caskets");
    public static Tag.Identified<Block> GRAVE_MARKERS = build("grave_markers");
    //Anything that can be turned into Blood-Soaked Soil
    public static Tag.Identified<Block> DIRT = build("dirt");

    private static Tag.Identified<Block> build(String name) {
        return TagFactory.BLOCK.create(new Identifier(Overlord.MODID, name));
    }
}
