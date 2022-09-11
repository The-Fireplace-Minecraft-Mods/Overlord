package dev.the_fireplace.overlord.network.client.builder;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;

@Singleton
public final class IssueLocalOrdersBufferBuilder
{
    public FriendlyByteBuf build() {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        return buffer;
    }
}
