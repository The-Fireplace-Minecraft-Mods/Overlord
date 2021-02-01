package the_fireplace.overlord.tags;

import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import the_fireplace.overlord.OverlordHelper;

public final class OverlordBlockTags {
    public static Tag<Block> CASKETS = build("caskets");
    public static Tag<Block> GRAVE_MARKERS = build("grave_markers");
    //Anything that can be turned into Blood-Soaked Soil
    public static Tag<Block> DIRT = build("dirt");

    private static Tag<Block> build(String name) {
        return new Tag.Builder<Block>().build(new Identifier(OverlordHelper.MODID, name));
    }
}
