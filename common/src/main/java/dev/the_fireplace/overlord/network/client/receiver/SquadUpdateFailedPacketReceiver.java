package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketReceiver;
import dev.the_fireplace.overlord.client.gui.squad.EditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public final class SquadUpdateFailedPacketReceiver implements ClientboundPacketReceiver
{
    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf) {
        List<Component> failureMessages = new ArrayList<>();
        while (buf.isReadable()) {
            failureMessages.add(buf.readComponent());
        }
        client.submit(() -> {
            Screen currentScreen = client.screen;
            if (currentScreen instanceof EditScreen editScreen) {
                editScreen.onFailedCreation(failureMessages);
            }
        });
    }
}
