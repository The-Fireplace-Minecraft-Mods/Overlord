package dev.the_fireplace.overlord.impl.registry;

import com.google.common.collect.Lists;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.data.objects.Pattern;
import dev.the_fireplace.overlord.domain.registry.PatternRegistry;
import dev.the_fireplace.overlord.util.MissingPattern;
import dev.the_fireplace.overlord.util.PatternImpl;
import net.minecraft.resources.ResourceLocation;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Singleton
@Implementation
public final class PatternRegistryImpl implements PatternRegistry
{
    private final Map<ResourceLocation, Pattern> patterns = new LinkedHashMap<>();

    public PatternRegistryImpl() {
        Lists.newArrayList(
            createSimple("white_bed"),
            createSimple("orange_bed"),
            createSimple("magenta_bed"),
            createSimple("light_blue_bed"),
            createSimple("yellow_bed"),
            createSimple("lime_bed"),
            createSimple("pink_bed"),
            createSimple("gray_bed"),
            createSimple("light_gray_bed"),
            createSimple("cyan_bed"),
            createSimple("purple_bed"),
            createSimple("blue_bed"),
            createSimple("brown_bed"),
            createSimple("green_bed"),
            createSimple("red_bed"),
            createSimple("black_bed"),
            createSimple("red_gold_checkerboard"),
            createSimple("pink_black_checkerboard"),
            createSimple("battle_worn")
        ).forEach(pattern -> patterns.put(pattern.getId(), pattern));
    }

    private Pattern createSimple(String patternPath) {
        return new PatternImpl(new ResourceLocation(OverlordConstants.MODID, patternPath));
    }

    @Override
    public List<Pattern> getPatterns() {
        return new ArrayList<>(patterns.values());
    }

    @Override
    public Pattern getById(ResourceLocation id) {
        return patterns.getOrDefault(id, new MissingPattern());
    }

    @Override
    public boolean hasPattern(ResourceLocation id) {
        return patterns.containsKey(id);
    }
}
