package dev.the_fireplace.overlord.client.gui.squad;

import com.google.inject.Injector;
import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.google.inject.name.Names;
import dev.the_fireplace.annotateddi.api.DIContainer;
import dev.the_fireplace.lib.api.chat.injectables.TextStyles;
import dev.the_fireplace.lib.api.uuid.injectables.EmptyUUID;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.gui.PartialScreen;
import dev.the_fireplace.overlord.domain.data.SquadPatterns;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import dev.the_fireplace.overlord.domain.registry.PatternRegistry;
import dev.the_fireplace.overlord.network.ClientToServerPacketIDs;
import dev.the_fireplace.overlord.network.client.builder.UpdateSquadBufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.*;

@Environment(EnvType.CLIENT)
public class EditScreen extends Screen
{
    private final String SQUAD_NAME_FIELD_TITLE = I18n.translate("gui.overlord.create_squad.squad_name");

    private final EmptyUUID emptyUUID;
    private final SquadPatterns squadPatterns;
    private final TextStyles textStyles;
    private final PatternRegistry patternRegistry;
    private final SelectorScreen parent;
    private final Collection<ItemStack> stacks;
    private final UUID squadId;
    private final PatternSelectionScreenPart.State patternState;
    private final ItemSelectionScreenPart.State itemState;
    private String squadName = "";

    private ButtonWidget confirmButton;
    private boolean saving = false;
    private List<Text> errors = Collections.emptyList();

    protected EditScreen(SelectorScreen parent, Collection<ItemStack> squadItems, @Nullable Squad currentSquad) {
        super(new TranslatableText("gui.overlord.create_squad.name"));
        Injector injector = DIContainer.get();
        this.emptyUUID = injector.getInstance(EmptyUUID.class);
        this.squadPatterns = injector.getInstance(Key.get(SquadPatterns.class, Names.named("client")));
        this.textStyles = injector.getInstance(TextStyles.class);
        this.patternRegistry = injector.getInstance(PatternRegistry.class);
        this.parent = parent;
        this.stacks = squadItems;
        Identifier patternId = new Identifier("");
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
        TextFieldWidget squadNameField = new TextFieldWidget(this.font, this.width * 3 / 4 - 100, 20, 200, 20, SQUAD_NAME_FIELD_TITLE);
        squadNameField.setText(this.squadName);
        squadNameField.setChangedListener(newSquadName -> {
            this.squadName = newSquadName;
            updateConfirmButtonEnabled();
        });
        this.children.add(squadNameField);
        ButtonWidget.PressAction confirmAction = (button) -> {
            ClientPlayNetworking.send(
                ClientToServerPacketIDs.UPDATE_SQUAD,
                UpdateSquadBufferBuilder.build(
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
        confirmButton = new ButtonWidget(this.width / 2 - 202, this.height - 30, 200, 20, I18n.translate("gui.overlord.confirm_exit"), confirmAction);
        this.addButton(confirmButton);
        updateConfirmButtonEnabled();
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height - 30, 200, 20, I18n.translate("gui.cancel"), (button) -> {
            closeScreen();
        }));
    }

    private <T extends Element & Drawable> void addPartialScreenChildren(PartialScreen widget) {
        for (Element child : widget.getChildren()) {
            //noinspection unchecked
            this.children.add((T) child);
        }
    }

    private void closeScreen() {
        MinecraftClient.getInstance().openScreen(parent);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        for (Element child : children) {
            //noinspection SuspiciousMethodCalls
            if (!buttons.contains(child) && child instanceof Drawable) {
                ((Drawable) child).render(mouseX, mouseY, delta);
            }
        }
        super.render(mouseX, mouseY, delta);
        this.font.draw(SQUAD_NAME_FIELD_TITLE, this.width * 3f / 4f - font.getStringWidth(SQUAD_NAME_FIELD_TITLE) / 2f, 4, 0xFFFFFF);
    }

    public void onSuccessfulCreation(Squad createdSquad) {
        parent.displaySquad(createdSquad);
        closeScreen();
    }

    public void onFailedCreation(List<Text> reasons) {
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
        List<Text> errors = new ArrayList<>();
        boolean hasLookupPreventingErrors = false;
        if (squadName.isEmpty()) {
            errors.add(createStyledError("gui.overlord.create_squad.name_required"));
        }
        Identifier patternId = patternState.getPatternId();
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
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        Objects.requireNonNull(player, "Client player cannot be null when creating squads!");
        if (!patternRegistry.getById(patternId).canBeUsedBy(player)) {
            Overlord.getLogger().error("Locked pattern warning produced on client, this should not be allowed!");
            errors.add(createStyledError("gui.overlord.create_squad.locked_pattern"));
        }

        if (!emptyUUID.is(squadId)) {
            if (!squadPatterns.isPatternUnusedByOtherSquads(patternId, stack, player.getUuid(), squadId)) {
                errors.add(createStyledError("gui.overlord.create_squad.pattern_taken"));
            }
        } else if (!squadPatterns.isPatternUnused(patternId, stack)) {
            errors.add(createStyledError("gui.overlord.create_squad.pattern_taken"));
        }

        this.errors = errors;
    }

    private Text createStyledError(String s) {
        return new TranslatableText(s).setStyle(textStyles.red());
    }
}
