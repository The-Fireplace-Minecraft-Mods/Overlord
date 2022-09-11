package dev.the_fireplace.overlord.domain.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;

public interface AdvancementProgressFinder
{
    AdvancementProgress getProgress(Player player, Advancement advancement);
}
