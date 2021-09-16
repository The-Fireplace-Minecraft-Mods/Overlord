package dev.the_fireplace.overlord.domain.internal.network.client;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import net.minecraft.util.PacketByteBuf;

public interface SaveAIPacketBufferBuilder {
    PacketByteBuf build(OrderableEntity orderableEntity);
}
