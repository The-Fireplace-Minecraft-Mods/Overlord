package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ClientboundPacketReceiver;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.client.gui.squad.EditScreen;
import dev.the_fireplace.overlord.client.util.SquadDeserialization;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.inject.Singleton;

@Singleton
public final class SquadUpdatedPacketReceiver implements ClientboundPacketReceiver
{
    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf) {
        CompoundTag squadCompound = buf.readNbt();
        if (squadCompound == null) {
            OverlordConstants.getLogger().error("Received squad updated packet with null squads!");
            return;
        }
        client.submit(() -> {
            Squad updatedSquad = SquadDeserialization.fromNbt(squadCompound);
            Screen currentScreen = client.screen;
            if (currentScreen instanceof EditScreen editScreen) {
                editScreen.onSuccessfulCreation(updatedSquad);
            }
        });
    }
}
