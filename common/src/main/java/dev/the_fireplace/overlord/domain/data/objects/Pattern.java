package dev.the_fireplace.overlord.domain.data.objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public interface Pattern
{
    ResourceLocation getId();

    boolean canBeUsedBy(Player player);

    ResourceLocation getTextureLocation();
}
