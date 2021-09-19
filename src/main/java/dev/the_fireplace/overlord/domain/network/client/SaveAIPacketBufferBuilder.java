package dev.the_fireplace.overlord.domain.network.client;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import net.minecraft.util.PacketByteBuf;

public interface SaveAIPacketBufferBuilder {
    PacketByteBuf build(OrderableEntity orderableEntity);
}
