package dev.the_fireplace.overlord.tags;

import dev.the_fireplace.overlord.Overlord;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class OverlordItemTags {
    public static Tag<Item> CASKETS = build("caskets");
    public static Tag<Item> GRAVE_MARKERS = build("grave_markers");
    public static Tag<Item> FLESH = build("flesh");
    public static Tag<Item> MUSCLE_MEAT = build("muscle_meat");

    private static Tag<Item> build(String name) {
        return new Tag.Builder<Item>().build(new Identifier(Overlord.MODID, name));
    }
}
