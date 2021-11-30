package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class SquadCreatorGui extends Screen
{
    private final SquadSelectorGui parent;

    protected SquadCreatorGui(SquadSelectorGui parent) {
        super(new TranslatableText("gui.overlord.create_squad.name"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        //TODO cape selector
        //TODO item slot/selector
        //TODO name textbox
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 202, this.height - 30, 200, 20, Text.of("Confirm and exit"), (button) -> {
            //TODO send save packet and disable, response should close GUI
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, this.height - 30, 200, 20, Text.of("Cancel"), (button) -> {
            closeScreen();
        }));
    }

    private void closeScreen() {
        MinecraftClient.getInstance().openScreen(parent);
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
