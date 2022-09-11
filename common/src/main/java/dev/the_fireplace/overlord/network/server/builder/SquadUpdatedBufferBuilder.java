package dev.the_fireplace.overlord.network.server.builder;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.util.SquadSerialization;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;

@Singleton
public final class SquadUpdatedBufferBuilder
{
    public FriendlyByteBuf build(Squad squad) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeNbt(SquadSerialization.toNbt(squad));
        return buffer;
    }
}
