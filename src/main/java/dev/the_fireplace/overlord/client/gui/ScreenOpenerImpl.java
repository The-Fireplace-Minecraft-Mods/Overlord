package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.client.gui.squad.SelectorScreen;
import dev.the_fireplace.overlord.domain.client.GuiOpener;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.network.client.builder.GetOrdersBufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

import javax.inject.Inject;
import javax.inject.Named;

@Implementation
@Environment(EnvType.CLIENT)
public final class ScreenOpenerImpl implements GuiOpener
{
    private final Squads squads;
    private final MinecraftClient client;

    @Inject
    public ScreenOpenerImpl(
        @Named("client") Squads squads
    ) {
        this.squads = squads;
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void openOrdersGUI(OrderableEntity entity) {
        ClientPlayNetworking.send(ClientToServerPacketIDs.GET_ORDERS, GetOrdersBufferBuilder.build(entity.getEntityIdNumber()));
    }

    @Override
    public void openSquadSelectorGUI(ArmyEntity entity) {
        client.openScreen(new SelectorScreen(
            new TranslatableText("gui.overlord.squad_manager.name"),
            client.currentScreen,
            squads.getSquadsWithOwner(entity.getOwnerUuid()),
            entity.getEntityIdNumber(),
            entity.getSquad()
        ));
    }
}
