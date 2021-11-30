package dev.the_fireplace.overlord.network.server.builder;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.network.server.OpenSquadsGUIBufferBuilder;
import dev.the_fireplace.overlord.util.SquadSerialization;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

@Implementation
public final class OpenSquadsGUIBufferBuilderImpl implements OpenSquadsGUIBufferBuilder
{
    @Override
    public PacketByteBuf buildSelector(int aiEntityID, Collection<? extends Squad> squads, @Nullable UUID currentSquad) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeNbt(SquadSerialization.collectionToNbt(squads));
        buffer.writeInt(aiEntityID);
        if (currentSquad != null) {
            buffer.writeUuid(currentSquad);
        }
        return buffer;
    }

    @Override
    public PacketByteBuf buildManager(Collection<? extends Squad> squads) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeNbt(SquadSerialization.collectionToNbt(squads));
        return buffer;
    }
}
