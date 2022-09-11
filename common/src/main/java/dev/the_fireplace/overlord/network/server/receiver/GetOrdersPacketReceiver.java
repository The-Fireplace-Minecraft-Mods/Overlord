package dev.the_fireplace.overlord.network.server.receiver;

import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.lib.api.network.interfaces.ServerboundPacketReceiver;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.entity.Ownable;
import dev.the_fireplace.overlord.network.ClientboundPackets;
import dev.the_fireplace.overlord.network.server.builder.OpenOrdersGUIBufferBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public final class GetOrdersPacketReceiver implements ServerboundPacketReceiver
{
    private final ClientboundPackets clientboundPackets;
    private final PacketSender packetSender;
    private final OpenOrdersGUIBufferBuilder openOrdersGUIBufferBuilder;

    @Inject
    public GetOrdersPacketReceiver(ClientboundPackets clientboundPackets, PacketSender packetSender, OpenOrdersGUIBufferBuilder openOrdersGUIBufferBuilder) {
        this.clientboundPackets = clientboundPackets;
        this.packetSender = packetSender;
        this.openOrdersGUIBufferBuilder = openOrdersGUIBufferBuilder;
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf) {
        int entityId = buf.readInt();
        //TODO Check which thread this runs on
        Entity entity = player.getCommandSenderWorld().getEntity(entityId);
        if (!(entity instanceof OrderableEntity)) {
            OverlordConstants.getLogger().info("Entity is not orderable: {}", Objects.toString(entity));
            return;
        }

        if (entity instanceof Ownable && !Objects.equals(((Ownable) entity).getOwnerUUID(), player.getUUID())) {
            OverlordConstants.getLogger().warn("Get Orders packet received with wrong player ID, expected {} and got {}.", ((Ownable) entity).getOwnerUUID(), player.getUUID());
            return;
        }
        FriendlyByteBuf ordersPacketBuffer = openOrdersGUIBufferBuilder.build(entityId, ((OrderableEntity) entity).getAISettings());
        packetSender.sendToClient(handler, clientboundPackets.openOrdersScreen(), ordersPacketBuffer);
    }
}
