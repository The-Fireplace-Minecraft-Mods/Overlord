package dev.the_fireplace.overlord.domain.data;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.UUID;

public interface SquadPatterns
{
    boolean isPatternUnused(Identifier patternId, ItemStack stack);

    boolean isPatternUnusedByOtherSquads(Identifier patternId, ItemStack stack, UUID owner, UUID squadId);
}
