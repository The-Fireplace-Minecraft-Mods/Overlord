package dev.the_fireplace.overlord.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public final class MissingDependencyScreen extends Screen
{
    private final Screen parent;
    private final Component message;

    public MissingDependencyScreen(Screen parent) {
        super(Component.translatable("gui.overlord.missing_dependency.title"));
        this.parent = parent;
        this.message = Component.translatable("gui.overlord.missing_dependency.requirements_or");
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 30, 200, 20, Component.translatable("gui.cancel"), (button) -> {
            closeScreen();
        }));
        this.addRenderableWidget(new Button(this.width / 2 - 100, 90, 200, 20, Component.literal("Cloth Config"), (button) -> {
            ClickEvent clothUrlClickEvent = new ClickEvent(
                ClickEvent.Action.OPEN_URL,
                "https://modrinth.com/mod/cloth-config"
            );
            this.handleComponentClicked(Style.EMPTY.withClickEvent(clothUrlClickEvent));
        }));
    }

    private void closeScreen() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        this.renderDirtBackground(0);
        drawCenteredString(poseStack, this.font, this.message, this.width / 2, 70, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, delta);
    }
}
