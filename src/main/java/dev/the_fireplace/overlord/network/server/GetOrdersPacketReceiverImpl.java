package dev.the_fireplace.overlord.network.server;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.api.entity.OrderableEntity;
import dev.the_fireplace.overlord.api.internal.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.api.internal.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.api.internal.network.server.GetOrdersPacketReceiver;
import dev.the_fireplace.overlord.api.internal.network.server.OpenOrdersGUIBufferBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import java.util.Objects;

public final class GetOrdersPacketReceiverImpl implements GetOrdersPacketReceiver {
    @Deprecated
    public static final GetOrdersPacketReceiver INSTANCE = new GetOrdersPacketReceiverImpl();

    private GetOrdersPacketReceiverImpl() {}

    @Override
    public Identifier getId() {
        return ClientToServerPacketIDs.getInstance().getOrdersPacketID();
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

        PacketByteBuf ordersPacketBuffer = OpenOrdersGUIBufferBuilder.getInstance().build(entityId, ((OrderableEntity) entity).getAISettings());
        responseSender.sendPacket(ServerToClientPacketIDs.getInstance().openOrdersGuiPacketID(), ordersPacketBuffer);
    }
}
