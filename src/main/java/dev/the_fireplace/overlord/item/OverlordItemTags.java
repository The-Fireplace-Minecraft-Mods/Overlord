package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.Overlord;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class OverlordItemTags
{
    public static TagKey<Item> CASKETS = build("caskets");
    public static TagKey<Item> GRAVE_MARKERS = build("grave_markers");
    public static TagKey<Item> FLESH = build("flesh");
    public static TagKey<Item> MUSCLE_MEAT = build("muscle_meat");

    public static TagKey<Item> DYES = buildCommon("dyes");
    public static TagKey<Item> BONES = buildCommon("bones");

    private static TagKey<Item> build(String name) {
        return TagKey.of(Registry.ITEM_KEY, new Identifier(Overlord.MODID, name));
    }

    private static TagKey<Item> buildCommon(String name) {
        return TagKey.of(Registry.ITEM_KEY, new Identifier("c", name));
    }
}
