package dev.the_fireplace.overlord.network.client.builder;

import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;

@Singleton
public final class UpdateOrdersBufferBuilder
{
    public FriendlyByteBuf buildForEntity(OrderableEntity orderableEntity) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeInt(orderableEntity.getEntityIdNumber());
        buffer.writeNbt(orderableEntity.getAISettings().toTag());

        return buffer;
    }

    public FriendlyByteBuf buildForWand(AISettings settings) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        buffer.writeInt(-1);
        buffer.writeNbt(settings.toTag());

        return buffer;
    }
}
