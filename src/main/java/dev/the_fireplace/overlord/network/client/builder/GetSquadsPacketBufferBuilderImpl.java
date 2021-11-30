package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.network.client.GetSquadsPacketBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

@Implementation
public final class GetSquadsPacketBufferBuilderImpl implements GetSquadsPacketBufferBuilder
{
    @Override
    public PacketByteBuf buildSquadSelector(int entityID) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(entityID);
        return buffer;
    }

    @Override
    public PacketByteBuf buildSquadManager() {
        return PacketByteBufs.create();
    }
}
