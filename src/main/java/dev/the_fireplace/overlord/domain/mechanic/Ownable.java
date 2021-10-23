package dev.the_fireplace.overlord.domain.mechanic;

import net.minecraft.entity.Entity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface Ownable
{
    UUID getOwnerId();

    @Nullable
    Entity getOwner();
}
