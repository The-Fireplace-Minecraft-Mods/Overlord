package dev.the_fireplace.overlord.domain.data.objects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public interface Pattern
{
    Identifier getId();

    boolean canBeUsedBy(PlayerEntity player);

    Identifier getTextureLocation();
}
