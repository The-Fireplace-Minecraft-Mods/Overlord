package dev.the_fireplace.overlord.impl.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;

public interface ProgressFinderProxy<T extends Player>
{
    AdvancementProgress find(T player, Advancement advancement);
}
