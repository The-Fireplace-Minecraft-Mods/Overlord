package dev.the_fireplace.overlord.network.client;

import dev.the_fireplace.overlord.api.entity.OrderableEntity;
import dev.the_fireplace.overlord.api.internal.network.client.SaveAIPacketBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;

public final class SaveAIPacketBufferBuilderImpl implements SaveAIPacketBufferBuilder {
    @Deprecated
    public static final SaveAIPacketBufferBuilder INSTANCE = new SaveAIPacketBufferBuilderImpl();

    private SaveAIPacketBufferBuilderImpl() {}

    @Override
    public PacketByteBuf build(OrderableEntity orderableEntity) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(orderableEntity.getEntityId());
        buffer.writeCompoundTag(orderableEntity.getAISettings().toTag());

        return buffer;
    }
}
