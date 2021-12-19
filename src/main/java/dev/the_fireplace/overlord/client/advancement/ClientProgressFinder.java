package dev.the_fireplace.overlord.client.advancement;

import dev.the_fireplace.overlord.impl.advancement.ProgressFinderProxy;
import dev.the_fireplace.overlord.mixin.client.ClientAdvancementManagerAccessor;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.network.ClientPlayerEntity;

public final class ClientProgressFinder implements ProgressFinderProxy<ClientPlayerEntity>
{
    @Override
    public AdvancementProgress find(ClientPlayerEntity player, Advancement advancement) {
        return ((ClientAdvancementManagerAccessor) player.networkHandler.getAdvancementHandler()).getAdvancementProgresses().getOrDefault(advancement, new AdvancementProgress());
    }
}
