package dev.the_fireplace.overlord.tags;

import dev.the_fireplace.overlord.Overlord;
import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class OverlordItemTags
{
    public static Tag.Identified<Item> CASKETS = build("caskets");
    public static Tag.Identified<Item> GRAVE_MARKERS = build("grave_markers");
    public static Tag.Identified<Item> FLESH = build("flesh");
    public static Tag.Identified<Item> MUSCLE_MEAT = build("muscle_meat");

    private static Tag.Identified<Item> build(String name) {
        return TagFactory.ITEM.create(new Identifier(Overlord.MODID, name));
    }
}
