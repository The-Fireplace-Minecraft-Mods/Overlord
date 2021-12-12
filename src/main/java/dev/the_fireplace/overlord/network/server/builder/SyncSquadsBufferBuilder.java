package dev.the_fireplace.overlord.network.server.builder;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.util.SquadSerialization;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;

import java.util.Collection;
import java.util.UUID;

public final class SyncSquadsBufferBuilder
{
    public static PacketByteBuf build(Collection<? extends Squad> squads) {
        PacketByteBuf buffer = PacketByteBufs.create();
        CompoundTag squadsNbt = SquadSerialization.collectionToNbt(squads);
        buffer.writeCompoundTag(squadsNbt);
        return buffer;
    }

    public static PacketByteBuf buildForOneOwner(UUID owner, Collection<? extends Squad> squads) {
        PacketByteBuf buffer = PacketByteBufs.create();
        CompoundTag squadsNbt = SquadSerialization.collectionToNbt(squads);
        buffer.writeCompoundTag(squadsNbt);
        buffer.writeUuid(owner);
        return buffer;
    }
}
