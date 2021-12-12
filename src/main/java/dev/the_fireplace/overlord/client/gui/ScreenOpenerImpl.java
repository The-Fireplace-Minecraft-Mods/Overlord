package dev.the_fireplace.overlord.client.gui;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.client.gui.squad.SelectorScreen;
import dev.the_fireplace.overlord.domain.client.ScreenOpener;
import dev.the_fireplace.overlord.domain.data.Squads;
import dev.the_fireplace.overlord.domain.entity.OrderableEntity;
import dev.the_fireplace.overlord.entity.ArmyEntity;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.network.client.builder.GetOrdersBufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.UUID;

@Implementation
@Environment(EnvType.CLIENT)
public final class ScreenOpenerImpl implements ScreenOpener
{
    private final Squads squads;
    private final EmptyUUID emptyUUID;
    private final MinecraftClient client;

    @Inject
    public ScreenOpenerImpl(
        @Named("client") Squads squads,
        EmptyUUID emptyUUID
    ) {
        this.squads = squads;
        this.emptyUUID = emptyUUID;
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void openOrdersGUI(OrderableEntity entity) {
        ClientPlayNetworking.send(ClientToServerPacketIDs.GET_ORDERS, GetOrdersBufferBuilder.build(entity.getEntityIdNumber()));
    }

    @Override
    public void openSquadSelectorGUI(@Nullable ArmyEntity entity) {
        Screen selectorScreen;
        if (entity != null) {
            selectorScreen = new SelectorScreen(
                new TranslatableText("gui.overlord.squad_manager.name"),
                client.currentScreen,
                squads.getSquadsWithOwner(entity.getOwnerUuid()),
                entity.getEntityIdNumber(),
                entity.getSquad()
            );
        } else {
            Objects.requireNonNull(client.player);
            ItemStack activeWand = OrdersWandItem.getActiveWand(client.player);
            //noinspection ConstantConditions
            UUID currentSquad = activeWand.hasTag() && activeWand.getTag().contains("squad")
                ? activeWand.getTag().getUuid("squad")
                : emptyUUID.get();
            selectorScreen = new SelectorScreen(
                new TranslatableText("gui.overlord.squad_manager.name"),
                client.currentScreen,
                squads.getSquadsWithOwner(client.player.getUuid()),
                null,
                currentSquad
            );
        }
        client.openScreen(selectorScreen);
    }
}
