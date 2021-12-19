package dev.the_fireplace.overlord.domain.registry;

import dev.the_fireplace.overlord.domain.data.objects.Pattern;
import net.minecraft.util.Identifier;

import java.util.List;

public interface PatternRegistry
{
    List<Pattern> getPatterns();

    Pattern getById(Identifier id);

    boolean hasPattern(Identifier id);
}
