package dev.the_fireplace.overlord.impl.advancement;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.advancement.AdvancementProgressFinder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.player.PlayerEntity;

@Implementation
public final class AdvancementProgressFinderImpl implements AdvancementProgressFinder
{
    @Override
    public AdvancementProgress getProgress(PlayerEntity player, Advancement advancement) {
        return ProgressFinderProxies.getFinder(player).find(player, advancement);
    }
}
