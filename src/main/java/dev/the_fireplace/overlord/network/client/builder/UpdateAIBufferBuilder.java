package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.util.PacketByteBuf;

@Environment(EnvType.CLIENT)
public final class UpdateAIBufferBuilder
{
    public static PacketByteBuf build(OrderableEntity orderableEntity) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(orderableEntity.getEntityIdNumber());
        buffer.writeCompoundTag(orderableEntity.getAISettings().toTag());

        return buffer;
    }
}
