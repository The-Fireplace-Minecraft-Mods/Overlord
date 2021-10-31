package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.client.GuiOpener;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.domain.network.client.GetOrdersPacketBufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import javax.inject.Inject;

@Implementation
@Environment(EnvType.CLIENT)
public final class GuiOpenerImpl implements GuiOpener
{
    private final ClientToServerPacketIDs clientToServerPacketIDs;
    private final GetOrdersPacketBufferBuilder getOrdersPacketBufferBuilder;

    @Inject
    public GuiOpenerImpl(ClientToServerPacketIDs clientToServerPacketIDs, GetOrdersPacketBufferBuilder getOrdersPacketBufferBuilder) {
        this.clientToServerPacketIDs = clientToServerPacketIDs;
        this.getOrdersPacketBufferBuilder = getOrdersPacketBufferBuilder;
    }

    @Override
    public void openOrdersGUI(OrderableEntity entity) {
        ClientPlayNetworking.send(clientToServerPacketIDs.getOrdersPacketID(), getOrdersPacketBufferBuilder.build(entity.getEntityIdNumber()));
    }
}
