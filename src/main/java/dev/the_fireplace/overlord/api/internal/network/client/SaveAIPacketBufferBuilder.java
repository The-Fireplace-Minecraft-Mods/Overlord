package dev.the_fireplace.overlord.api.internal.network.client;

import dev.the_fireplace.overlord.api.entity.OrderableEntity;
import dev.the_fireplace.overlord.network.client.SaveAIPacketBufferBuilderImpl;
import net.minecraft.util.PacketByteBuf;

public interface SaveAIPacketBufferBuilder {
    static SaveAIPacketBufferBuilder getInstance() {
        //noinspection deprecation
        return SaveAIPacketBufferBuilderImpl.INSTANCE;
    }
    PacketByteBuf build(OrderableEntity orderableEntity);
}
