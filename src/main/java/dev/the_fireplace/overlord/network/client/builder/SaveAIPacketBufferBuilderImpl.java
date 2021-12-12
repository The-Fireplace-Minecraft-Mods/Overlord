package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.network.client.SaveAIPacketBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

@Implementation
public final class SaveAIPacketBufferBuilderImpl implements SaveAIPacketBufferBuilder {
    @Override
    public PacketByteBuf build(OrderableEntity orderableEntity) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(orderableEntity.getEntityIdNumber());
        buffer.writeNbt(orderableEntity.getAISettings().toTag());

        return buffer;
    }
}
