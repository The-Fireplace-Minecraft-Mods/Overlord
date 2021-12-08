package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.lib.api.network.interfaces.ClientPacketReceiver;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.gui.squad.EditScreen;
import dev.the_fireplace.overlord.client.util.SquadDeserialization;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.network.ServerToClientPacketIDs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class SquadUpdatedPacketReceiver implements ClientPacketReceiver
{
    @Override
    public Identifier getId() {
        return ServerToClientPacketIDs.SQUAD_UPDATED;
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        NbtCompound squadCompound = buf.readNbt();
        if (squadCompound == null) {
            Overlord.getLogger().error("Received sync squads packet with null squads!");
            return;
        }
        client.submit(() -> {
            Squad updatedSquad = SquadDeserialization.fromNbt(squadCompound);
            Screen currentScreen = client.currentScreen;
            if (currentScreen instanceof EditScreen editScreen) {
                editScreen.onSuccessfulCreation(updatedSquad);
            }
        });
    }
}
