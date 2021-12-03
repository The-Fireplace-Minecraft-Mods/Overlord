package dev.the_fireplace.overlord.util;

import com.google.common.collect.Lists;
import net.minecraft.util.Identifier;

import java.util.List;

public class SquadPatterns
{
    public static List<String> getPatterns() {
        return Lists.newArrayList(
            "white_bed",
            "orange_bed",
            "magenta_bed",
            "light_blue_bed",
            "yellow_bed",
            "lime_bed",
            "pink_bed",
            "gray_bed",
            "light_gray_bed",
            "cyan_bed",
            "purple_bed",
            "blue_bed",
            "brown_bed",
            "green_bed",
            "red_bed",
            "black_bed"
        );
    }

    public static Identifier getTextureForPatternId(Identifier pattern) {
        return new Identifier(pattern.getNamespace(), "textures/entity/cape/" + pattern.getPath() + ".png");
    }
}
