package dev.the_fireplace.overlord.impl.advancement;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;

public final class ServerProgressFinder implements ProgressFinderProxy<ServerPlayer>
{
    @Override
    public AdvancementProgress find(ServerPlayer player, Advancement advancement) {
        return player.getAdvancements().getOrStartProgress(advancement);
    }
}
