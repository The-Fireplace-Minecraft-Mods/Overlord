package dev.the_fireplace.overlord.network.server;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.internal.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.domain.internal.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.domain.internal.network.server.GetOrdersPacketReceiver;
import dev.the_fireplace.overlord.domain.internal.network.server.OpenOrdersGUIBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import javax.inject.Inject;
import java.util.Objects;

@Implementation
public final class GetOrdersPacketReceiverImpl implements GetOrdersPacketReceiver {

    private final ClientToServerPacketIDs clientToServerPacketIDs;
    private final ServerToClientPacketIDs serverToClientPacketIDs;
    private final OpenOrdersGUIBufferBuilder openOrdersGUIBufferBuilder;

    @Inject
    public GetOrdersPacketReceiverImpl(
        ClientToServerPacketIDs clientToServerPacketIDs,
        ServerToClientPacketIDs serverToClientPacketIDs,
        OpenOrdersGUIBufferBuilder openOrdersGUIBufferBuilder
    ) {
        this.clientToServerPacketIDs = clientToServerPacketIDs;
        this.serverToClientPacketIDs = serverToClientPacketIDs;
        this.openOrdersGUIBufferBuilder = openOrdersGUIBufferBuilder;
    }

    @Override
    public Identifier getId() {
        return clientToServerPacketIDs.getOrdersPacketID();
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        //TODO check player proximity and ownership
        int entityId = buf.readInt();
        //TODO Check which thread this runs on
        Entity entity = player.getEntityWorld().getEntityById(entityId);
        if (!(entity instanceof OrderableEntity)) {
            Overlord.getLogger().info("Entity is not orderable: {}", Objects.toString(entity));
            return;
        }

        PacketByteBuf ordersPacketBuffer = openOrdersGUIBufferBuilder.build(entityId, ((OrderableEntity) entity).getAISettings());
        responseSender.sendPacket(serverToClientPacketIDs.openOrdersGuiPacketID(), ordersPacketBuffer);
    }
}
