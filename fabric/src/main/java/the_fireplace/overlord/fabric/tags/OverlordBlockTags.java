package the_fireplace.overlord.fabric.tags;

import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import the_fireplace.overlord.OverlordHelper;

public final class OverlordBlockTags {
    public static Tag<Block> CASKETS = build("caskets");

    private static Tag<Block> build(String name) {
        return new Tag.Builder<Block>().build(new Identifier(OverlordHelper.MODID, name));
    }
}
