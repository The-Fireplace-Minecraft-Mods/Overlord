package dev.the_fireplace.overlord.domain.data;

import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface SquadPatterns
{
    boolean isPatternUnused(String pattern, ItemStack stack);

    boolean isPatternUnusedByOtherSquads(String pattern, ItemStack stack, UUID owner, UUID squadId);

    boolean canUsePattern(UUID player, String pattern);
}
