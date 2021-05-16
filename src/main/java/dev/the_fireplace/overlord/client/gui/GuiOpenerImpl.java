package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.overlord.api.client.GuiOpener;
import dev.the_fireplace.overlord.api.entity.OrderableEntity;
import dev.the_fireplace.overlord.api.internal.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.api.internal.network.client.GetOrdersPacketBufferBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class GuiOpenerImpl implements GuiOpener {
    @Deprecated
    public static final GuiOpener INSTANCE = new GuiOpenerImpl();

    private GuiOpenerImpl() {}

    @Override
    public void openOrdersGUI(OrderableEntity entity) {
        ClientPlayNetworking.send(ClientToServerPacketIDs.getInstance().getOrdersPacketID(), GetOrdersPacketBufferBuilder.getInstance().build(entity.getEntityId()));
    }
}
