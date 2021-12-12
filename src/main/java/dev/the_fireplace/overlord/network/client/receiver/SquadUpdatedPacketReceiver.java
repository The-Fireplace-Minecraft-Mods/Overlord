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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

@Environment(EnvType.CLIENT)
public final class SquadUpdatedPacketReceiver implements ClientPacketReceiver
{
    @Override
    public Identifier getId() {
        return ServerToClientPacketIDs.SQUAD_UPDATED;
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        CompoundTag squadCompound = buf.readCompoundTag();
        if (squadCompound == null) {
            Overlord.getLogger().error("Received squad updated packet with null squads!");
            return;
        }
        client.submit(() -> {
            Squad updatedSquad = SquadDeserialization.fromNbt(squadCompound);
            Screen currentScreen = client.currentScreen;
            if (currentScreen instanceof EditScreen) {
                ((EditScreen) currentScreen).onSuccessfulCreation(updatedSquad);
            }
        });
    }
}
