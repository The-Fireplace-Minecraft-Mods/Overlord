package dev.the_fireplace.overlord.domain.network.server;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.network.PacketByteBuf;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public interface OpenSquadsGUIBufferBuilder
{
    PacketByteBuf buildSelector(int aiEntityID, Collection<? extends Squad> squads, @Nullable UUID currentSquad);

    PacketByteBuf buildManager(Collection<? extends Squad> squads);
}
