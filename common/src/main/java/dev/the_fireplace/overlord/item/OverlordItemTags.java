package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.loader.TagHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public final class OverlordItemTags
{
    public static Tag.Named<Item> CASKETS = build("caskets");
    public static Tag.Named<Item> GRAVE_MARKERS = build("grave_markers");
    public static Tag.Named<Item> FLESH = build("flesh");
    public static Tag.Named<Item> MUSCLE_MEAT = build("muscle_meat");

    public static Tag.Named<Item> DYES = buildCommon("dyes");
    public static Tag.Named<Item> BONES = buildCommon("bones");

    private static Tag.Named<Item> build(String name) {
        TagHelper tagHelper = OverlordConstants.getInjector().getInstance(TagHelper.class);
        return tagHelper.createItemTag(new ResourceLocation(OverlordConstants.MODID, name));
    }

    private static Tag.Named<Item> buildCommon(String name) {
        TagHelper tagHelper = OverlordConstants.getInjector().getInstance(TagHelper.class);
        return tagHelper.createItemTag(new ResourceLocation("c", name));
    }
}
