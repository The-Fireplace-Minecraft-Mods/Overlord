package dev.the_fireplace.overlord.client.advancement;

import dev.the_fireplace.overlord.impl.advancement.ProgressFinderProxy;
import dev.the_fireplace.overlord.mixin.client.ClientAdvancementManagerAccessor;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.player.LocalPlayer;

public final class ClientProgressFinder implements ProgressFinderProxy<LocalPlayer>
{
    @Override
    public AdvancementProgress find(LocalPlayer player, Advancement advancement) {
        return ((ClientAdvancementManagerAccessor) player.connection.getAdvancements()).getProgress().getOrDefault(advancement, new AdvancementProgress());
    }
}
