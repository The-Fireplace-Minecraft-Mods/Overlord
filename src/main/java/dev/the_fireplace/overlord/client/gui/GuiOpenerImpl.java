package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.overlord.api.client.GuiOpener;
import dev.the_fireplace.overlord.api.entity.OrderableEntity;
import dev.the_fireplace.overlord.api.network.c2sPackets.GetOrdersPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class GuiOpenerImpl implements GuiOpener {
    @Deprecated
    public static final GuiOpener INSTANCE = new GuiOpenerImpl();

    private GuiOpenerImpl() {}

    @Override
    public void openOrdersGUI(OrderableEntity entity) {
        ClientPlayNetworking.send(GetOrdersPacket.getInstance().getId(), GetOrdersPacket.getInstance().buildBuffer(entity.getEntityId()));
    }
}
