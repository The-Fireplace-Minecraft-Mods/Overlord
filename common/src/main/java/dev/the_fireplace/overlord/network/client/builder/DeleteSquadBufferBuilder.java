package dev.the_fireplace.overlord.network.client.builder;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public final class DeleteSquadBufferBuilder
{
    public FriendlyByteBuf build(UUID squadId) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeUUID(squadId);
        return buffer;
    }
}
