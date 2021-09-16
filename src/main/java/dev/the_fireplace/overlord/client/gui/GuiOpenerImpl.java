package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.client.GuiOpener;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.domain.internal.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.domain.internal.network.client.GetOrdersPacketBufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Implementation
@Environment(EnvType.CLIENT)
public final class GuiOpenerImpl implements GuiOpener {
    @Override
    public void openOrdersGUI(OrderableEntity entity) {
        ClientPlayNetworking.send(ClientToServerPacketIDs.getInstance().getOrdersPacketID(), GetOrdersPacketBufferBuilder.getInstance().build(entity.getEntityId()));
    }
}
