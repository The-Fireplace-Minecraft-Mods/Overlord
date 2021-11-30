package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.client.GuiOpener;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.domain.network.client.GetOrdersPacketBufferBuilder;
import dev.the_fireplace.overlord.domain.network.client.GetSquadsPacketBufferBuilder;
import dev.the_fireplace.overlord.entity.ArmyEntity;
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
    private final GetSquadsPacketBufferBuilder getSquadsPacketBufferBuilder;

    @Inject
    public GuiOpenerImpl(ClientToServerPacketIDs clientToServerPacketIDs, GetOrdersPacketBufferBuilder getOrdersPacketBufferBuilder, GetSquadsPacketBufferBuilder getSquadsPacketBufferBuilder) {
        this.clientToServerPacketIDs = clientToServerPacketIDs;
        this.getOrdersPacketBufferBuilder = getOrdersPacketBufferBuilder;
        this.getSquadsPacketBufferBuilder = getSquadsPacketBufferBuilder;
    }

    @Override
    public void openOrdersGUI(OrderableEntity entity) {
        ClientPlayNetworking.send(clientToServerPacketIDs.getOrdersPacketID(), getOrdersPacketBufferBuilder.build(entity.getEntityIdNumber()));
    }

    @Override
    public void openSquadManagerGUI() {
        ClientPlayNetworking.send(clientToServerPacketIDs.getSquadsPacketID(), getSquadsPacketBufferBuilder.buildSquadManager());
    }

    @Override
    public void openSquadSelectorGUI(ArmyEntity entity) {
        ClientPlayNetworking.send(clientToServerPacketIDs.getSquadsPacketID(), getSquadsPacketBufferBuilder.buildSquadSelector(entity.getEntityIdNumber()));
    }
}
