package dev.the_fireplace.overlord.network.client;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.domain.client.OrdersGuiFactory;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.domain.network.client.OpenOrdersGUIPacketReceiver;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

import javax.inject.Inject;
import java.util.Objects;

@Environment(EnvType.CLIENT)
@Implementation
public final class OpenOrdersGUIPacketReceiverImpl implements OpenOrdersGUIPacketReceiver {

    private final ServerToClientPacketIDs serverToClientPacketIDs;
    private final OrdersGuiFactory ordersGuiFactory;

    @Inject
    public OpenOrdersGUIPacketReceiverImpl(ServerToClientPacketIDs serverToClientPacketIDs, OrdersGuiFactory ordersGuiFactory) {
        this.serverToClientPacketIDs = serverToClientPacketIDs;
        this.ordersGuiFactory = ordersGuiFactory;
    }

    @Override
    public Identifier getId() {
        return serverToClientPacketIDs.openOrdersGuiPacketID();
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.world == null) {
            Overlord.getLogger().error("Received open orders packet with null client world!");
            return;
        }
        Entity entity = client.world.getEntityById(buf.readInt());
        if (!(entity instanceof OrderableEntity)) {
            Overlord.getLogger().info("Received open orders packet for non orderable entity: {}", Objects.toString(entity));
            return;
        }
        CompoundTag aiCompound = buf.readCompoundTag();
        if (aiCompound == null) {
            Overlord.getLogger().error("Received open orders packet with null ai settings!");
            return;
        }
        ((OrderableEntity) entity).updateAISettings(aiCompound);
        Screen parentScreen = client.currentScreen;
        if (parentScreen == null) {
            Overlord.getLogger().warn("Parent screen is null, attempting to open orders GUI anyways!");
        }
        client.openScreen(ordersGuiFactory.build(parentScreen, (OrderableEntity) entity));
    }
}
