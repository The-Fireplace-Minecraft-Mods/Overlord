package dev.the_fireplace.overlord.network.client.builder;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;

@Singleton
public final class GetOrdersBufferBuilder
{
    public FriendlyByteBuf build(int entityId) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeInt(entityId);
        return buffer;
    }
}
