package dev.the_fireplace.overlord.network.server.builder;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public final class SquadUpdateFailedBufferBuilder
{
    public FriendlyByteBuf build(Collection<Component> errorMessages) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        for (Component errorMessage : errorMessages) {
            buffer.writeComponent(errorMessage);
        }
        return buffer;
    }
}
