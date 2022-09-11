package dev.the_fireplace.overlord.client.gui.squad;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.lib.api.chat.injectables.TextStyles;
import dev.the_fireplace.lib.api.network.injectables.PacketSender;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.client.gui.PartialScreen;
import dev.the_fireplace.overlord.domain.data.SquadPatterns;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.registry.PatternRegistry;
import dev.the_fireplace.overlord.network.ServerboundPackets;
import dev.the_fireplace.overlord.network.client.builder.UpdateSquadBufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;

public class EditScreen extends Screen
{
    private static final TranslatableComponent SQUAD_NAME_FIELD_TITLE = new TranslatableComponent("gui.overlord.create_squad.squad_name");

    private final EmptyUUID emptyUUID;
    private final SquadPatterns squadPatterns;
    private final TextStyles textStyles;
    private final PatternRegistry patternRegistry;
    private final PacketSender packetSender;
    private final ServerboundPackets serverboundPackets;
    private final UpdateSquadBufferBuilder updateSquadBufferBuilder;
    private final SelectorScreen parent;
    private final Collection<ItemStack> stacks;
    private final UUID squadId;
    private final PatternSelectionScreenPart.State patternState;
    private final ItemSelectionScreenPart.State itemState;
    private String squadName = "";

    private Button confirmButton;
    private boolean saving = false;
    private List<Component> errors = Collections.emptyList();

    protected EditScreen(SelectorScreen parent, Collection<ItemStack> squadItems, @Nullable Squad currentSquad) {
        super(new TranslatableComponent("gui.overlord.create_squad.name"));
        Injector injector = OverlordConstants.getInjector();
        this.emptyUUID = injector.getInstance(EmptyUUID.class);
        this.squadPatterns = injector.getInstance(Key.get(SquadPatterns.class, Names.named("client")));
        this.textStyles = injector.getInstance(TextStyles.class);
        this.patternRegistry = injector.getInstance(PatternRegistry.class);
        this.packetSender = injector.getInstance(PacketSender.class);
        this.serverboundPackets = injector.getInstance(ServerboundPackets.class);
        this.updateSquadBufferBuilder = injector.getInstance(UpdateSquadBufferBuilder.class);
        this.parent = parent;
        this.stacks = squadItems;
        ResourceLocation patternId = new ResourceLocation("");
        ItemStack stack = ItemStack.EMPTY;
        if (currentSquad != null) {
            squadName = currentSquad.getName();
            patternId = currentSquad.getPatternId();
            stack = currentSquad.getItem();
            squadId = currentSquad.getSquadId();
        } else {
            squadId = emptyUUID.get();
        }
        this.patternState = new PatternSelectionScreenPart.State(patternId, identifier -> updateConfirmButtonEnabled());
        this.itemState = new ItemSelectionScreenPart.State(stack, updatedStack -> updateConfirmButtonEnabled());
    }

    @Override
    protected void init() {
        PatternSelectionScreenPart patternSelectionScreenPart = new PatternSelectionScreenPart(4, 4, this.width / 2 - 4, this.height - 4 - 30, patternState);
        ItemSelectionScreenPart itemSelectionScreenPart = new ItemSelectionScreenPart(this.width / 2, 44, this.width / 2 - 4, this.height - 30 - 4 - 44, this.stacks, itemState);
        this.addPartialScreenChildren(patternSelectionScreenPart);
        this.addPartialScreenChildren(itemSelectionScreenPart);
        EditBox squadNameField = new EditBox(this.font, this.width * 3 / 4 - 100, 20, 200, 20, SQUAD_NAME_FIELD_TITLE);
        squadNameField.setValue(this.squadName);
        squadNameField.setResponder(newSquadName -> {
            this.squadName = newSquadName;
            updateConfirmButtonEnabled();
        });
        this.addButton(squadNameField);
        Button.OnPress confirmAction = (button) -> {
            packetSender.sendToServer(
                serverboundPackets.updateSquad(),
                updateSquadBufferBuilder.build(
                    squadId,
                    squadName,
                    patternState.getPatternId(),
                    itemState.getStack(),
                    parent.getEntityId()
                )
            );
            this.saving = true;
            updateConfirmButtonEnabled();
        };
        Button.OnTooltip confirmTooltipSupplier = (buttonWidget, matrixStack, i, j) -> EditScreen.this.renderComponentTooltip(matrixStack, errors, i, j);
        confirmButton = new Button(this.width / 2 - 202, this.height - 30, 200, 20, new TranslatableComponent("gui.overlord.confirm_exit"), confirmAction, confirmTooltipSupplier);
        this.addButton(confirmButton);
        updateConfirmButtonEnabled();
        this.addButton(new Button(this.width / 2 + 2, this.height - 30, 200, 20, new TranslatableComponent("gui.cancel"), (button) -> {
            closeScreen();
        }));
    }

    private <T extends GuiEventListener & Widget> void addPartialScreenChildren(PartialScreen widget) {
        for (GuiEventListener child : widget.getChildren()) {
            //noinspection unchecked
            this.addWidget((T) child);
        }
    }

    private void closeScreen() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        for (GuiEventListener child : children) {
            //noinspection SuspiciousMethodCalls
            if (!buttons.contains(child) && child instanceof Widget) {
                ((Widget) child).render(matrices, mouseX, mouseY, delta);
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
        this.font.draw(matrices, SQUAD_NAME_FIELD_TITLE, this.width * 3f / 4f - font.width(SQUAD_NAME_FIELD_TITLE) / 2f, 4, 0xFFFFFF);
    }

    public void onSuccessfulCreation(Squad createdSquad) {
        parent.displaySquad(createdSquad);
        closeScreen();
    }

    public void onFailedCreation(List<Component> reasons) {
        this.errors = reasons;
        this.confirmButton.active = false;
        this.saving = false;
    }

    private void updateConfirmButtonEnabled() {
        if (saving) {
            this.errors = Lists.newArrayList(createStyledError("gui.overlord.create_squad.saving"));
            this.confirmButton.active = false;
        } else {
            calculateErrors();
            this.confirmButton.active = this.errors.isEmpty();
        }
    }

    private void calculateErrors() {
        List<Component> errors = new ArrayList<>();
        boolean hasLookupPreventingErrors = false;
        if (squadName.isEmpty()) {
            errors.add(createStyledError("gui.overlord.create_squad.name_required"));
        }
        ResourceLocation patternId = patternState.getPatternId();
        if (!patternRegistry.hasPattern(patternId)) {
            hasLookupPreventingErrors = true;
            errors.add(createStyledError("gui.overlord.create_squad.pattern_required"));
        }
        ItemStack stack = itemState.getStack();
        if (stack.isEmpty()) {
            hasLookupPreventingErrors = true;
            errors.add(createStyledError("gui.overlord.create_squad.item_required"));
        }
        if (hasLookupPreventingErrors) {
            this.errors = errors;
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        Objects.requireNonNull(player, "Client player cannot be null when creating squads!");
        if (!patternRegistry.getById(patternId).canBeUsedBy(player)) {
            OverlordConstants.getLogger().error("Locked pattern warning produced on client, this should not be allowed!");
            errors.add(createStyledError("gui.overlord.create_squad.locked_pattern"));
        }

        if (!emptyUUID.is(squadId)) {
            if (!squadPatterns.isPatternUnusedByOtherSquads(patternId, stack, player.getUUID(), squadId)) {
                errors.add(createStyledError("gui.overlord.create_squad.pattern_taken"));
            }
        } else if (!squadPatterns.isPatternUnused(patternId, stack)) {
            errors.add(createStyledError("gui.overlord.create_squad.pattern_taken"));
        }

        this.errors = errors;
    }

    private Component createStyledError(String s) {
        return new TranslatableComponent(s).setStyle(textStyles.red());
    }
}
