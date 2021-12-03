package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.client.gui.PartialScreen;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SquadCreatorGui extends Screen
{
    private static final TranslatableText SQUAD_NAME_FIELD_TITLE = new TranslatableText("gui.overlord.create_squad.squad_name");

    private final SquadSelectorGui parent;

    private TextFieldWidget squadNameWidget;

    protected SquadCreatorGui(SquadSelectorGui parent) {
        super(new TranslatableText("gui.overlord.create_squad.name"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        PatternSelectionScreenPart patternSelectionScreenPart = new PatternSelectionScreenPart(4, 4, this.width / 2 - 4, this.height - 4 - 30);
        this.addPartialScreenChildren(patternSelectionScreenPart);

        //TODO item slot/selector
        this.addDrawableChild(squadNameWidget = new TextFieldWidget(this.textRenderer, this.width * 3 / 4 - 100, 20, 200, 20, SQUAD_NAME_FIELD_TITLE));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 202, this.height - 30, 200, 20, new TranslatableText("gui.overlord.confirm_exit"), (button) -> {
            //TODO send save packet and disable, response should close GUI
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, this.height - 30, 200, 20, new TranslatableText("gui.cancel"), (button) -> {
            closeScreen();
        }));
    }

    private <T extends Element & Drawable & Selectable> void addPartialScreenChildren(PartialScreen widget) {
        for (Element child : widget.getChildren()) {
            //noinspection unchecked
            this.addDrawableChild((T) child);
        }
    }

    private void closeScreen() {
        MinecraftClient.getInstance().openScreen(parent);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.textRenderer.draw(matrices, SQUAD_NAME_FIELD_TITLE, this.width * 3f / 4f - textRenderer.getWidth(SQUAD_NAME_FIELD_TITLE) / 2f, 4, 0xFFFFFF);
    }

    public void onSuccessfulCreation(Squad createdSquad) {
        parent.displayNewSquad(createdSquad);
        closeScreen();
    }

    public void onFailedCreation(String reason) {//TODO Text?
        //TODO show error message
    }

    public void onErrorFieldChange() {
        //TODO enable confirm button and clear message
    }
}
