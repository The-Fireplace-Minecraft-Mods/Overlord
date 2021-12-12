package dev.the_fireplace.overlord.client.gui.squad;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.client.gui.rendertools.DrawEntity;
import dev.the_fireplace.overlord.client.gui.rendertools.OverlayButtonWidget;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.rule.SquadEligibleItems;
import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.item.OrdersWandItem;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.network.client.builder.DeleteSquadBufferBuilder;
import dev.the_fireplace.overlord.network.client.builder.SetSquadBufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class SelectorScreen extends Screen
{
    private final EmptyUUID emptyUUID;
    private final SquadEligibleItems squadEligibleItems;
    private final Screen parent;
    private final Collection<Squad> ownedSquads;
    @Nullable
    private final Integer entityId;
    @Nullable
    private final UUID currentSquad;
    private SelectorWidget selectorWidget;
    private ButtonWidget editButton;
    private ButtonWidget deleteButton;
    private UUID selectedSquad;
    private OwnedSkeletonEntity renderedSkeleton;
    private long openTime;

    public SelectorScreen(Text title, Screen parent, Collection<? extends Squad> ownedSquads, @Nullable Integer entityId, @Nullable UUID currentSquad) {
        super(title);
        this.emptyUUID = DIContainer.get().getInstance(EmptyUUID.class);
        this.squadEligibleItems = DIContainer.get().getInstance(SquadEligibleItems.class);
        this.parent = parent;
        this.currentSquad = currentSquad;
        this.entityId = entityId;
        this.ownedSquads = Lists.newArrayList(ownedSquads);
        this.selectedSquad = currentSquad;
    }

    @Override
    protected void init() {
        selectorWidget = createSquadSelector();
        this.children.add(selectorWidget);
        this.addButton(new ButtonWidget(this.width / 2 - 202, this.height - 30, 200, 20, new TranslatableText("gui.overlord.confirm_exit"), (button) -> {
            if (entityId != null) {
                ClientPlayNetworking.send(ClientToServerPacketIDs.SET_SQUAD, SetSquadBufferBuilder.buildForEntity(selectedSquad, entityId));
            } else {
                ClientPlayNetworking.send(ClientToServerPacketIDs.SET_SQUAD, SetSquadBufferBuilder.buildForWand(selectedSquad));
                Objects.requireNonNull(client);
                Objects.requireNonNull(client.player);
                OrdersWandItem.getActiveWand(client.player).getOrCreateTag().putUuid("squad", selectedSquad);
            }
            closeScreen();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height - 30, 200, 20, new TranslatableText("gui.cancel"), (button) -> {
            closeScreen();
        }));
        this.addButton(editButton = new OverlayButtonWidget(0, this.height - 54, this.width / 3, 20, Text.of(""), (button) -> {
            Collection<ItemStack> squadItems = getSquadItems();
            Squad currentSquad = ownedSquads.stream().filter(squad -> squad.getSquadId().equals(selectedSquad)).findFirst().orElse(null);
            this.client.openScreen(new EditScreen(this, squadItems, currentSquad));
        }));
        this.addButton(deleteButton = new ButtonWidget(this.width - 102, 2, 100, 20, new TranslatableText("gui.overlord.squad_manager.delete_squad"), (button) -> {
            ClientPlayNetworking.send(ClientToServerPacketIDs.DELETE_SQUAD, DeleteSquadBufferBuilder.build(selectedSquad));
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
        if (client != null && client.world != null) {
            renderedSkeleton = OwnedSkeletonEntity.create(client.world, null);
            if (entityId != null) {
                Entity entity = client.world.getEntityById(entityId);
                if (entity != null) {
                    renderedSkeleton.copyFrom(entity);
                }
            }
            renderedSkeleton.setPos(0, 0, 0);
        }
    }

    private void updateButtons() {
        if (editButton != null) {
            editButton.setMessage(selectedSquad != null
                ? new TranslatableText("gui.overlord.squad_manager.edit_squad")
                : new TranslatableText("gui.overlord.squad_manager.create_squad")
            );
        }
        if (deleteButton != null) {
            deleteButton.active = selectedSquad != null;
        }
    }

    private Collection<ItemStack> getSquadItems() {
        Objects.requireNonNull(this.client);
        Entity entity = this.entityId != null && this.client.world != null ? this.client.world.getEntityById(entityId) : null;
        ClientPlayerEntity player = this.client.player;
        return this.squadEligibleItems.getEligibleItems(ownedSquads, player, entity);
    }

    private SelectorWidget createSquadSelector() {
        SelectorWidget selectorWidget = new SelectorWidget(
            this.client,
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
        selectorWidget.selectSquad(currentSquad);
        return selectorWidget;
    }

    private void closeScreen() {
        MinecraftClient.getInstance().openScreen(parent);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        //TODO draw selected squad's data
        DrawEntity.drawEntityFacingAway(width / 2, height / 2 + 50, 100, this.openTime, System.currentTimeMillis(), renderedSkeleton);
        super.render(matrixStack, mouseX, mouseY, delta);
    }

    public void displaySquad(Squad squad) {
        Optional<Squad> existingSquad = findSquadById(squad.getSquadId());
        if (existingSquad.isPresent()) {
            ownedSquads.remove(existingSquad.get());
            selectorWidget.removeSquad(existingSquad.get());
        }
        ownedSquads.add(squad);
        selectorWidget.addSquads(Sets.newHashSet(squad));
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
