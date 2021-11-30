package dev.the_fireplace.overlord.domain.network.client;

import net.minecraft.network.PacketByteBuf;

public interface GetSquadsPacketBufferBuilder
{
    PacketByteBuf buildSquadSelector(int entityID);

    PacketByteBuf buildSquadManager();
}
