package dev.the_fireplace.overlord.network.client.receiver;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.gui.squad.SquadSelectorGui;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.network.ServerToClientPacketIDs;
import dev.the_fireplace.overlord.domain.network.client.OpenSquadsGUIPacketReceiver;
import dev.the_fireplace.overlord.util.SquadSerialization;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import javax.inject.Inject;
import java.util.Collection;
import java.util.UUID;

@Environment(EnvType.CLIENT)
@Implementation
public final class OpenSquadsGUIPacketReceiverImpl implements OpenSquadsGUIPacketReceiver
{
    private final ServerToClientPacketIDs serverToClientPacketIDs;

    @Inject
    public OpenSquadsGUIPacketReceiverImpl(ServerToClientPacketIDs serverToClientPacketIDs) {
        this.serverToClientPacketIDs = serverToClientPacketIDs;
    }

    @Override
    public Identifier getId() {
        return serverToClientPacketIDs.openSquadsGuiPacketID();
    }

    @Override
    public void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        NbtCompound squadCompound = buf.readNbt();
        if (squadCompound == null) {
            Overlord.getLogger().error("Received open squads packet with null squads!");
            return;
        }
        Collection<? extends Squad> playerSquads = SquadSerialization.collectionFromNbt(squadCompound);
        boolean isSelector = buf.isReadable();
        Integer entityId = isSelector ? buf.readInt() : null;
        boolean hasCurrentSquad = buf.isReadable();
        UUID currentSquad = hasCurrentSquad ? buf.readUuid() : null;
        Screen parentScreen = client.currentScreen;
        if (parentScreen == null) {
            Overlord.getLogger().warn("Parent screen is null, attempting to open squads GUI anyways!");
        }
        client.submit(() -> client.openScreen(new SquadSelectorGui(
            new TranslatableText("gui.overlord.squad_manager.name"),
            parentScreen,
            playerSquads,
            entityId,
            currentSquad
        )));
    }
}
