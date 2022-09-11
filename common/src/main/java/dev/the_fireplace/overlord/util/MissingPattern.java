package dev.the_fireplace.overlord.util;

import dev.the_fireplace.overlord.domain.data.objects.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class MissingPattern implements Pattern
{
    @Override
    public ResourceLocation getId() {
        return new ResourceLocation("");
    }

    @Override
    public boolean canBeUsedBy(Player player) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return new ResourceLocation("");
    }
}
