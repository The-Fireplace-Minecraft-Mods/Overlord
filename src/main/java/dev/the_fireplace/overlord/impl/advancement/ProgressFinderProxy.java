package dev.the_fireplace.overlord.impl.advancement;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.player.PlayerEntity;

public interface ProgressFinderProxy<T extends PlayerEntity>
{
    AdvancementProgress find(T player, Advancement advancement);
}
