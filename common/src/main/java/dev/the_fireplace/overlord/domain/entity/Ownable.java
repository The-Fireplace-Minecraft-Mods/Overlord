package dev.the_fireplace.overlord.domain.entity;

import javax.annotation.Nullable;
import java.util.UUID;

public interface Ownable
{
    @Nullable
    UUID getOwnerUUID();

    void setOwnerUUID(@Nullable UUID uuid);
}
