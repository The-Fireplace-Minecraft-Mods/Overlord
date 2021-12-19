package dev.the_fireplace.overlord.domain.advancement;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.player.PlayerEntity;

public interface AdvancementProgressFinder
{
    AdvancementProgress getProgress(PlayerEntity player, Advancement advancement);
}
