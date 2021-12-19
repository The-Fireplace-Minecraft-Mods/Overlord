package dev.the_fireplace.overlord.impl.advancement;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ServerProgressFinder implements ProgressFinderProxy<ServerPlayerEntity>
{
    @Override
    public AdvancementProgress find(ServerPlayerEntity player, Advancement advancement) {
        return player.getAdvancementTracker().getProgress(advancement);
    }
}
