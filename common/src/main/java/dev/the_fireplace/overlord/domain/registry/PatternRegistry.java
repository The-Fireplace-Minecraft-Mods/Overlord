package dev.the_fireplace.overlord.domain.registry;

import dev.the_fireplace.overlord.domain.data.objects.Pattern;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface PatternRegistry
{
    List<Pattern> getPatterns();

    Pattern getById(ResourceLocation id);

    boolean hasPattern(ResourceLocation id);
}
