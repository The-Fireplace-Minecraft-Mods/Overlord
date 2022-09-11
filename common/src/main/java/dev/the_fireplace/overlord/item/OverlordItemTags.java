package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.loader.TagHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public final class OverlordItemTags
{
    public static Tag<Item> CASKETS = build("caskets");
    public static Tag<Item> GRAVE_MARKERS = build("grave_markers");
    public static Tag<Item> FLESH = build("flesh");
    public static Tag<Item> MUSCLE_MEAT = build("muscle_meat");

    public static Tag<Item> DYES = buildCommon("dyes");
    public static Tag<Item> BONES = buildCommon("bones");

    private static Tag<Item> build(String name) {
        TagHelper tagHelper = OverlordConstants.getInjector().getInstance(TagHelper.class);
        return tagHelper.createItemTag(new ResourceLocation(OverlordConstants.MODID, name));
    }

    private static Tag<Item> buildCommon(String name) {
        TagHelper tagHelper = OverlordConstants.getInjector().getInstance(TagHelper.class);
        return tagHelper.createItemTag(new ResourceLocation("c", name));
    }
}
