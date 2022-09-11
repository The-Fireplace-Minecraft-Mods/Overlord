package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class OverlordItemTags
{
    public static TagKey<Item> CASKETS = build("caskets");
    public static TagKey<Item> GRAVE_MARKERS = build("grave_markers");
    public static TagKey<Item> FLESH = build("flesh");
    public static TagKey<Item> MUSCLE_MEAT = build("muscle_meat");

    public static TagKey<Item> DYES = buildCommon("dyes");
    public static TagKey<Item> BONES = buildCommon("bones");

    private static TagKey<Item> build(String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(OverlordConstants.MODID, name));
    }

    private static TagKey<Item> buildCommon(String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("c", name));
    }
}
