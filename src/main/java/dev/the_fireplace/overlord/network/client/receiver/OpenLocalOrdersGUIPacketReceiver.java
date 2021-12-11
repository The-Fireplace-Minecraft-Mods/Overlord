package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ClientPacketReceiver;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.gui.orders.LocalOrdersScreen;
import dev.the_fireplace.overlord.item.OverlordItems;
import dev.the_fireplace.overlord.network.ServerToClientPacketIDs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class OpenLocalOrdersGUIPacketReceiver implements ClientPacketReceiver
{
    @Override
    public Identifier getId() {
        return ServerToClientPacketIDs.OPEN_LOCAL_ORDERS_GUI;
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.world == null || client.player == null) {
            Overlord.getLogger().error("Received open local orders packet with null client world or player!");
            return;
        }
        int orderDistance = buf.readInt();
        ItemStack mainHandStack = client.player.getMainHandStack();
        ItemStack offHandStack = client.player.getOffHandStack();
        if (!mainHandStack.isOf(OverlordItems.ORDERS_WAND) && !offHandStack.isOf(OverlordItems.ORDERS_WAND)) {
            return;
        }
        client.submit(() -> client.openScreen(new LocalOrdersScreen(orderDistance)));
    }
}
