package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.Overlord;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public final class OverlordItemTags
{
    public static Tag<Item> CASKETS = build("caskets");
    public static Tag<Item> GRAVE_MARKERS = build("grave_markers");
    public static Tag<Item> FLESH = build("flesh");
    public static Tag<Item> MUSCLE_MEAT = build("muscle_meat");

    public static Tag<Item> DYES = buildCommon("dyes");

    private static Tag<Item> build(String name) {
        return TagRegistry.item(new Identifier(Overlord.MODID, name));
    }

    private static Tag<Item> buildCommon(String name) {
        return TagFactory.ITEM.create(new Identifier("c", name));
    }
}
