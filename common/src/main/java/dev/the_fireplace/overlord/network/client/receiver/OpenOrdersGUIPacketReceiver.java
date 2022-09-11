package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketReceiver;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.domain.client.OrdersGuiFactory;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;

@Singleton
public final class OpenOrdersGUIPacketReceiver implements ClientboundPacketReceiver
{
    private final OrdersGuiFactory ordersGuiFactory;

    @Inject
    public OpenOrdersGUIPacketReceiver(OrdersGuiFactory ordersGuiFactory) {
        this.ordersGuiFactory = ordersGuiFactory;
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf) {
        if (client.level == null) {
            OverlordConstants.getLogger().error("Received open orders packet with null client world!");
            return;
        }
        Entity entity = client.level.getEntity(buf.readInt());
        if (!(entity instanceof OrderableEntity)) {
            OverlordConstants.getLogger().info("Received open orders packet for non orderable entity: {}", Objects.toString(entity));
            return;
        }
        CompoundTag aiCompound = buf.readNbt();
        if (aiCompound == null) {
            OverlordConstants.getLogger().error("Received open orders packet with null ai settings!");
            return;
        }
        ((OrderableEntity) entity).updateAISettings(aiCompound);
        Screen parentScreen = client.screen;
        if (parentScreen == null) {
            OverlordConstants.getLogger().warn("Parent screen is null, attempting to open orders GUI anyways!");
        }
        client.submit(() -> client.setScreen(ordersGuiFactory.build(parentScreen, (OrderableEntity) entity)));
    }
}
