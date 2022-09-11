package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.client.gui.squad.SelectorScreen;
import dev.the_fireplace.overlord.domain.client.ScreenOpener;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import dev.the_fireplace.overlord.network.ServerboundPackets;
import dev.the_fireplace.overlord.network.client.builder.GetOrdersBufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.UUID;

@Implementation(environment = "CLIENT")
public final class ScreenOpenerImpl implements ScreenOpener
{
    private final Squads squads;
    private final EmptyUUID emptyUUID;
    private final PacketSender packetSender;
    private final ServerboundPackets serverboundPackets;
    private final GetOrdersBufferBuilder getOrdersBufferBuilder;
    private final Minecraft client;

    @Inject
    public ScreenOpenerImpl(
        @Named("client") Squads squads,
        EmptyUUID emptyUUID,
        PacketSender packetSender,
        ServerboundPackets serverboundPackets,
        GetOrdersBufferBuilder getOrdersBufferBuilder
    ) {
        this.squads = squads;
        this.emptyUUID = emptyUUID;
        this.packetSender = packetSender;
        this.serverboundPackets = serverboundPackets;
        this.getOrdersBufferBuilder = getOrdersBufferBuilder;
        this.client = Minecraft.getInstance();
    }

    @Override
    public void openOrdersGUI(OrderableEntity entity) {
        packetSender.sendToServer(serverboundPackets.getOrders(), getOrdersBufferBuilder.build(entity.getEntityIdNumber()));
    }

    @Override
    public void openSquadSelectorGUI(@Nullable ArmyEntity entity) {
        Screen selectorScreen;
        if (entity != null) {
            selectorScreen = new SelectorScreen(
                new TranslatableComponent("gui.overlord.squad_manager.name"),
                client.screen,
                squads.getSquadsWithOwner(entity.getOwnerUUID()),
                entity.getEntityIdNumber(),
                entity.getSquad()
            );
        } else {
            Objects.requireNonNull(client.player);
            ItemStack activeWand = OrdersWandItem.getActiveWand(client.player);
            //noinspection ConstantConditions
            UUID currentSquad = activeWand.hasTag() && activeWand.getTag().contains("squad")
                ? activeWand.getTag().getUUID("squad")
                : emptyUUID.get();
            selectorScreen = new SelectorScreen(
                new TranslatableComponent("gui.overlord.squad_manager.name"),
                client.screen,
                squads.getSquadsWithOwner(client.player.getUUID()),
                null,
                currentSquad
            );
        }
        client.setScreen(selectorScreen);
    }
}
