package dev.the_fireplace.overlord.client.gui.config;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.lib.api.client.interfaces.CustomButtonScreen;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.PositionSetting;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public class PositionSelectorGui extends Screen implements CustomButtonScreen<String>
{
    public static final Predicate<String> IS_NUMBER = (testStr) -> {
        try {
            Integer.parseInt(testStr);
            return true;
        } catch (NumberFormatException exception) {
            return false;
        }
    };

    private final Promise<Optional<String>> resultPromise;
    private final Screen parent;
    private final PositionSetting previousSelection;
    private final BlockPos currentPosition;
    private Button confirmButton;
    private EditBox xWidget;
    private EditBox yWidget;
    private EditBox zWidget;

    public PositionSelectorGui(Component title, Screen parent, String currentValue, @Nullable BlockPos currentPosition) {
        super(title);
        this.resultPromise = new DefaultPromise<>(new DefaultEventExecutor());
        this.parent = parent;
        this.previousSelection = PositionSetting.fromString(currentValue);
        this.currentPosition = currentPosition;
    }

    @Override
    public Promise<Optional<String>> getNewValuePromise() {
        return resultPromise;
    }

    @Override
    protected void init() {
        if (minecraft == null) {
            throw new IllegalStateException("Cannot initialize with null client!");
        }
        this.addRenderableWidget(xWidget = new EditBox(minecraft.font, this.width / 2 - 75 - 2, this.height / 2, 50, 20, Component.nullToEmpty("X")));
        this.addRenderableWidget(yWidget = new EditBox(minecraft.font, this.width / 2 - 25, this.height / 2, 50, 20, Component.nullToEmpty("Y")));
        this.addRenderableWidget(zWidget = new EditBox(minecraft.font, this.width / 2 + 25 + 2, this.height / 2, 50, 20, Component.nullToEmpty("Z")));
        Button currentPositionButton = new Button(this.width / 2 - 100, this.height / 2 - 30, 200, 20, new TranslatableComponent("gui.overlord.select_position.use_current"), (button) -> {
            setCoordinates(currentPosition.getX(), currentPosition.getY(), currentPosition.getZ());
        });
        this.addRenderableWidget(currentPositionButton);
        this.addRenderableWidget(confirmButton = new Button(this.width / 2 - 202, this.height - 30, 200, 20, new TranslatableComponent("gui.overlord.confirm_exit"), (button) -> {
            PositionSetting newPosition = new PositionSetting(Integer.parseInt(xWidget.getValue()), Integer.parseInt(yWidget.getValue()), Integer.parseInt(zWidget.getValue()));
            resultPromise.setSuccess(Optional.of(newPosition.toString()));
            onClose();
        }));
        this.addRenderableWidget(new Button(this.width / 2 + 2, this.height - 30, 200, 20, new TranslatableComponent("gui.cancel"), (button) -> {
            resultPromise.setSuccess(Optional.empty());
            onClose();
        }));
        currentPositionButton.visible = currentPosition != null;
        setCoordinates(previousSelection.getX(), previousSelection.getY(), previousSelection.getZ());
    }

    private void setCoordinates(int x, int y, int z) {
        xWidget.setValue(String.valueOf(x));
        yWidget.setValue(String.valueOf(y));
        zWidget.setValue(String.valueOf(z));
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        if (confirmButton.active && hasInvalidCoordinate()) {
            confirmButton.active = false;
        } else if (!confirmButton.active && !hasInvalidCoordinate()) {
            confirmButton.active = true;
        }
        super.render(matrixStack, mouseX, mouseY, delta);
    }

    private boolean hasInvalidCoordinate() {
        return !IS_NUMBER.test(xWidget.getValue())
            || !IS_NUMBER.test(yWidget.getValue())
            || !IS_NUMBER.test(zWidget.getValue());
    }
}
