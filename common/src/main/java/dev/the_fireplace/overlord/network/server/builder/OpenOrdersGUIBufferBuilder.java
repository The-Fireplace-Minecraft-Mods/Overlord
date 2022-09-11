package dev.the_fireplace.overlord.network.server.builder;

import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;

@Singleton
public final class OpenOrdersGUIBufferBuilder
{
    public FriendlyByteBuf build(int aiEntityID, AISettings entitySettings) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeInt(aiEntityID);
        buffer.writeNbt(entitySettings.toTag());
        return buffer;
    }
}
