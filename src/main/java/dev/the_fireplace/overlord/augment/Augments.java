package dev.the_fireplace.overlord.augment;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.registry.HeadBlockAugmentRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;

public final class Augments
{
    public static final Identifier IMPOSTER = create("imposter");
    public static final Identifier FRAGILE = create("fragile");
    public static final Identifier SLOW_BURN = create("slow_burn");

    public static void register(HeadBlockAugmentRegistry registry) {
        registry.register(Blocks.SKELETON_SKULL, IMPOSTER);
        registry.register(Blocks.GLASS, FRAGILE);
        registry.register(Blocks.BLACK_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.RED_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.BLUE_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.BROWN_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.CYAN_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.GRAY_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.GREEN_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.LIGHT_BLUE_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.LIGHT_GRAY_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.LIME_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.MAGENTA_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.ORANGE_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.PINK_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.PURPLE_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.WHITE_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.YELLOW_STAINED_GLASS, FRAGILE);
        registry.register(Blocks.COAL_BLOCK, SLOW_BURN);
    }

    private static Identifier create(String path) {
        return new Identifier(Overlord.MODID, path);
    }
}
