package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ClientPacketReceiver;
import dev.the_fireplace.overlord.client.gui.squad.EditScreen;
import dev.the_fireplace.overlord.network.ServerToClientPacketIDs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class SquadUpdateFailedPacketReceiver implements ClientPacketReceiver
{
    @Override
    public Identifier getId() {
        return ServerToClientPacketIDs.SQUAD_UPDATE_FAILED;
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        List<Text> failureMessages = new ArrayList<>();
        while (buf.isReadable()) {
            failureMessages.add(buf.readText());
        }
        client.submit(() -> {
            Screen currentScreen = client.currentScreen;
            if (currentScreen instanceof EditScreen) {
                ((EditScreen) currentScreen).onFailedCreation(failureMessages);
            }
        });
    }
}
