package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public final class UpdateAIBufferBuilder
{
    public static PacketByteBuf build(OrderableEntity orderableEntity) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(orderableEntity.getEntityIdNumber());
        buffer.writeNbt(orderableEntity.getAISettings().toTag());

        return buffer;
    }
}
