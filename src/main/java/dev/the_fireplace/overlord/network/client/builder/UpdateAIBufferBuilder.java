package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

@Environment(EnvType.CLIENT)
public final class UpdateAIBufferBuilder
{
    public static PacketByteBuf buildForEntity(OrderableEntity orderableEntity) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(orderableEntity.getEntityIdNumber());
        buffer.writeNbt(orderableEntity.getAISettings().toTag());

        return buffer;
    }

    public static PacketByteBuf buildForWand(AISettings settings) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(-1);
        buffer.writeNbt(settings.toTag());

        return buffer;
    }
}
