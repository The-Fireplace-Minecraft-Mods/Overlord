package dev.the_fireplace.overlord.impl.advancement;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.advancement.AdvancementProgressFinder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;

@Implementation
public final class AdvancementProgressFinderImpl implements AdvancementProgressFinder
{
    @Override
    public AdvancementProgress getProgress(Player player, Advancement advancement) {
        return ProgressFinderProxies.getFinder(player).find(player, advancement);
    }
}
