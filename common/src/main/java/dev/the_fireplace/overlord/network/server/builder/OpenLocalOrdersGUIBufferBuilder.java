package dev.the_fireplace.overlord.network.server.builder;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;

@Singleton
public final class OpenLocalOrdersGUIBufferBuilder
{
    public FriendlyByteBuf build(int localOrdersDistance) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeInt(localOrdersDistance);
        return buffer;
    }
}
