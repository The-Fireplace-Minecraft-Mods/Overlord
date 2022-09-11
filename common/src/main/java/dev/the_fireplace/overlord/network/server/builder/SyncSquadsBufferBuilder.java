package dev.the_fireplace.overlord.network.server.builder;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.util.SquadSerialization;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.UUID;

@Singleton
public final class SyncSquadsBufferBuilder
{
    public FriendlyByteBuf build(Collection<? extends Squad> squads) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        CompoundTag squadsNbt = SquadSerialization.collectionToNbt(squads);
        buffer.writeNbt(squadsNbt);
        return buffer;
    }

    public FriendlyByteBuf buildForOneOwner(UUID owner, Collection<? extends Squad> squads) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        CompoundTag squadsNbt = SquadSerialization.collectionToNbt(squads);
        buffer.writeNbt(squadsNbt);
        buffer.writeUUID(owner);
        return buffer;
    }
}
