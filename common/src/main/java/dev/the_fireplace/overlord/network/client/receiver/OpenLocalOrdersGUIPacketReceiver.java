package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketReceiver;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.client.gui.orders.LocalOrdersScreen;
import dev.the_fireplace.overlord.item.OverlordItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class OpenLocalOrdersGUIPacketReceiver implements ClientboundPacketReceiver
{
    private final OverlordItems overlordItems;

    @Inject
    public OpenLocalOrdersGUIPacketReceiver(OverlordItems overlordItems) {
        this.overlordItems = overlordItems;
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf) {
        if (client.level == null || client.player == null) {
            OverlordConstants.getLogger().error("Received open local orders packet with null client world or player!");
            return;
        }
        int orderDistance = buf.readInt();
        ItemStack mainHandStack = client.player.getMainHandItem();
        ItemStack offHandStack = client.player.getOffhandItem();
        if (!mainHandStack.is(overlordItems.getOrdersWand()) && !offHandStack.is(overlordItems.getOrdersWand())) {
            return;
        }
        client.submit(() -> client.setScreen(new LocalOrdersScreen(orderDistance)));
    }
}
