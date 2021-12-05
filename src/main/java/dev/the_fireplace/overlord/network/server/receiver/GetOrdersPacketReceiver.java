package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ServerPacketReceiver;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.network.server.builder.OpenOrdersGUIBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

public final class GetOrdersPacketReceiver implements ServerPacketReceiver
{
    @Override
    public Identifier getId() {
        return ClientToServerPacketIDs.GET_ORDERS;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        int entityId = buf.readInt();
        //TODO Check which thread this runs on
        Entity entity = player.getEntityWorld().getEntityById(entityId);
        if (!(entity instanceof OrderableEntity)) {
            Overlord.getLogger().info("Entity is not orderable: {}", Objects.toString(entity));
            return;
        }

        if (entity instanceof Ownable && !Objects.equals(((Ownable) entity).getOwnerUuid(), player.getUuid())) {
            Overlord.getLogger().warn("Get Orders packet received with wrong player ID, expected {} and got {}.", ((Ownable) entity).getOwnerUuid(), player.getUuid());
            return;
        }
        PacketByteBuf ordersPacketBuffer = OpenOrdersGUIBufferBuilder.build(entityId, ((OrderableEntity) entity).getAISettings());
        responseSender.sendPacket(ServerToClientPacketIDs.OPEN_ORDERS_GUI, ordersPacketBuffer);
    }
}
