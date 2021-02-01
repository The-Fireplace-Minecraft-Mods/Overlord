package the_fireplace.overlord.tags;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import the_fireplace.overlord.OverlordHelper;

public final class OverlordItemTags {
    public static Tag<Item> CASKETS = build("caskets");
    public static Tag<Item> GRAVE_MARKERS = build("grave_markers");
    public static Tag<Item> FLESH = build("flesh");
    public static Tag<Item> MUSCLE_MEAT = build("muscle_meat");

    private static Tag<Item> build(String name) {
        return new Tag.Builder<Item>().build(new Identifier(OverlordHelper.MODID, name));
    }
}
