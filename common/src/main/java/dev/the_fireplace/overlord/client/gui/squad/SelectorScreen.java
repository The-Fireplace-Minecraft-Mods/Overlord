package dev.the_fireplace.overlord.client.gui.squad;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.client.gui.rendertools.DrawEntity;
import dev.the_fireplace.overlord.client.gui.rendertools.OverlayButtonWidget;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.rule.SquadEligibleItems;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import dev.the_fireplace.overlord.network.ServerboundPackets;
import dev.the_fireplace.overlord.network.client.builder.DeleteSquadBufferBuilder;
import dev.the_fireplace.overlord.network.client.builder.SetSquadBufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class SelectorScreen extends Screen
{
    private final EmptyUUID emptyUUID;
    private final SquadEligibleItems squadEligibleItems;
    private final PacketSender packetSender;
    private final ServerboundPackets serverboundPackets;
    private final SetSquadBufferBuilder setSquadBufferBuilder;
    private final DeleteSquadBufferBuilder deleteSquadBufferBuilder;
    private final Screen parent;
    private final Collection<Squad> ownedSquads;
    @Nullable
    private final Integer entityId;
    private SelectorWidget selectorWidget;
    private Button editButton;
    private Button deleteButton;
    private UUID selectedSquad;
    private OwnedSkeletonEntity renderedSkeleton;
    private long openTime;

    public SelectorScreen(Component title, Screen parent, Collection<? extends Squad> ownedSquads, @Nullable Integer entityId, UUID currentSquad) {
        super(title);
        Injector injector = OverlordConstants.getInjector();
        this.emptyUUID = injector.getInstance(EmptyUUID.class);
        this.squadEligibleItems = injector.getInstance(SquadEligibleItems.class);
        this.packetSender = injector.getInstance(PacketSender.class);
        this.serverboundPackets = injector.getInstance(ServerboundPackets.class);
        this.setSquadBufferBuilder = injector.getInstance(SetSquadBufferBuilder.class);
        this.deleteSquadBufferBuilder = injector.getInstance(DeleteSquadBufferBuilder.class);
        this.parent = parent;
        this.entityId = entityId;
        this.ownedSquads = Lists.newArrayList(ownedSquads);
        this.selectedSquad = currentSquad;
    }

    @Override
    protected void init() {
        selectorWidget = createSquadSelector();
        this.addRenderableWidget(selectorWidget);
        this.addRenderableWidget(new Button(this.width / 2 - 202, this.height - 30, 200, 20, Component.translatable("gui.overlord.confirm_exit"), (button) -> {
            if (entityId != null) {
                packetSender.sendToServer(serverboundPackets.setSquad(), setSquadBufferBuilder.buildForEntity(selectedSquad, entityId));
            } else {
                packetSender.sendToServer(serverboundPackets.setSquad(), setSquadBufferBuilder.buildForWand(selectedSquad));
                Objects.requireNonNull(minecraft);
                Objects.requireNonNull(minecraft.player);
                OrdersWandItem.getActiveWand(minecraft.player).getOrCreateTag().putUUID("squad", selectedSquad);
            }
            closeScreen();
        }));
        this.addRenderableWidget(new Button(this.width / 2 + 2, this.height - 30, 200, 20, Component.translatable("gui.cancel"), (button) -> {
            closeScreen();
        }));
        this.addRenderableWidget(editButton = new OverlayButtonWidget(0, this.height - 54, this.width / 3, 20, Component.nullToEmpty(""), (button) -> {
            Collection<ItemStack> squadItems = getSquadItems();
            Squad currentSquad = ownedSquads.stream().filter(squad -> squad.getSquadId().equals(selectedSquad)).findFirst().orElse(null);
            this.minecraft.setScreen(new EditScreen(this, squadItems, currentSquad));
        }));
        this.addRenderableWidget(deleteButton = new Button(this.width - 102, 2, 100, 20, Component.translatable("gui.overlord.squad_manager.delete_squad"), (button) -> {
            packetSender.sendToServer(serverboundPackets.deleteSquad(), deleteSquadBufferBuilder.build(selectedSquad));
            Optional<Squad> selectedSquad = findSquadById(this.selectedSquad);
            if (selectedSquad.isPresent()) {
                ownedSquads.remove(selectedSquad.get());
                selectorWidget.removeSquad(selectedSquad.get());
            }
            selectorWidget.selectSquad(emptyUUID.get());
            renderedSkeleton.setSquad(emptyUUID.get());
        }));
        updateButtons();

        openTime = System.currentTimeMillis();
        if (minecraft != null && minecraft.level != null && renderedSkeleton == null) {
            renderedSkeleton = OwnedSkeletonEntity.create(minecraft.level, null);
            if (entityId != null) {
                Entity entity = minecraft.level.getEntity(entityId);
                if (entity instanceof OwnedSkeletonEntity) {
                    renderedSkeleton.restoreFrom(entity);
                }
            }
            renderedSkeleton.setPosRaw(0, 0, 0);
        }
    }

    private void updateButtons() {
        if (editButton != null) {
            editButton.setMessage(!emptyUUID.is(selectedSquad)
                ? Component.translatable("gui.overlord.squad_manager.edit_squad")
                : Component.translatable("gui.overlord.squad_manager.create_squad")
            );
        }
        if (deleteButton != null) {
            deleteButton.active = !emptyUUID.is(selectedSquad);
        }
    }

    private Collection<ItemStack> getSquadItems() {
        Objects.requireNonNull(this.minecraft);
        Entity entity = this.entityId != null && this.minecraft.level != null ? this.minecraft.level.getEntity(entityId) : null;
        LocalPlayer player = this.minecraft.player;
        return this.squadEligibleItems.getEligibleItems(ownedSquads, player, entity);
    }

    private SelectorWidget createSquadSelector() {
        SelectorWidget selectorWidget = new SelectorWidget(
            this.minecraft,
            this.width / 3,
            this.height - 52,
            0,
            this.height - 54,
            30,
            newSquadId -> {
                this.selectedSquad = newSquadId;
                if (this.renderedSkeleton != null) {
                    this.renderedSkeleton.setSquad(newSquadId);
                }
                this.updateButtons();
            });
        selectorWidget.addSquads(ownedSquads);
        selectorWidget.selectSquad(selectedSquad);
        return selectorWidget;
    }

    private void closeScreen() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        //TODO draw selected squad's data
        DrawEntity.drawEntityFacingAway(width / 2, height / 2 + 50, 75, this.openTime, System.currentTimeMillis(), renderedSkeleton);
        super.render(matrixStack, mouseX, mouseY, delta);
    }

    public void displaySquad(Squad squad) {
        Optional<Squad> existingSquad = findSquadById(squad.getSquadId());
        if (existingSquad.isPresent()) {
            ownedSquads.remove(existingSquad.get());
            selectorWidget.removeSquad(existingSquad.get());
        }
        ownedSquads.add(squad);
        selectorWidget.addSquads(Set.of(squad));
        this.selectedSquad = squad.getSquadId();
        selectorWidget.selectSquad(squad.getSquadId());
        if (this.renderedSkeleton != null) {
            renderedSkeleton.setSquad(squad.getSquadId());
        }
        updateButtons();
    }

    private Optional<Squad> findSquadById(UUID squadId) {
        return ownedSquads.stream().filter(ownedSquad -> ownedSquad.getSquadId().equals(squadId)).findFirst();
    }

    @Nullable
    public Integer getEntityId() {
        return entityId;
    }
}
