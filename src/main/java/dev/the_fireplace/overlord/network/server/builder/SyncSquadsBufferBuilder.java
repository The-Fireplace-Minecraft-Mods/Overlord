package dev.the_fireplace.overlord.network.server.builder;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.util.SquadSerialization;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.Collection;
import java.util.UUID;

public final class SyncSquadsBufferBuilder
{
    public static PacketByteBuf build(Collection<? extends Squad> squads) {
        PacketByteBuf buffer = PacketByteBufs.create();
        NbtCompound squadsNbt = SquadSerialization.collectionToNbt(squads);
        buffer.writeNbt(squadsNbt);
        return buffer;
    }

    public static PacketByteBuf buildForOneOwner(UUID owner, Collection<? extends Squad> squads) {
        PacketByteBuf buffer = PacketByteBufs.create();
        NbtCompound squadsNbt = SquadSerialization.collectionToNbt(squads);
        buffer.writeNbt(squadsNbt);
        buffer.writeUuid(owner);
        return buffer;
    }
}
