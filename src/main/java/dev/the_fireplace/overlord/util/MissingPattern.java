package dev.the_fireplace.overlord.util;

import dev.the_fireplace.overlord.domain.data.objects.Pattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public final class MissingPattern implements Pattern
{
    @Override
    public Identifier getId() {
        return new Identifier("");
    }

    @Override
    public boolean canBeUsedBy(PlayerEntity player) {
        return false;
    }

    @Override
    public Identifier getTextureLocation() {
        return new Identifier("");
    }
}
