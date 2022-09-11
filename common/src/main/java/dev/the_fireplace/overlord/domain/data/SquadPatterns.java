package dev.the_fireplace.overlord.domain.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public interface SquadPatterns
{
    boolean isPatternUnused(ResourceLocation patternId, ItemStack stack);

    boolean isPatternUnusedByOtherSquads(ResourceLocation patternId, ItemStack stack, UUID owner, UUID squadId);
}
