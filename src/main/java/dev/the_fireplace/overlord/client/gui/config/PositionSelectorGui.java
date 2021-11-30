package dev.the_fireplace.overlord.client.gui.config;

import dev.the_fireplace.lib.api.client.interfaces.CustomButtonScreen;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.PositionSetting;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
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
    private ButtonWidget confirmButton;
    private TextFieldWidget xWidget;
    private TextFieldWidget yWidget;
    private TextFieldWidget zWidget;

    public PositionSelectorGui(Text title, Screen parent, String currentValue, @Nullable BlockPos currentPosition) {
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
        if (client == null) {
            throw new IllegalStateException("Cannot initialize with null client!");
        }
        this.addButton(xWidget = new TextFieldWidget(minecraft.textRenderer, this.width / 2 - 75 - 2, this.height / 2, 50, 20, "X"));
        this.addButton(yWidget = new TextFieldWidget(minecraft.textRenderer, this.width / 2 - 25, this.height / 2, 50, 20, "Y"));
        this.addButton(zWidget = new TextFieldWidget(minecraft.textRenderer, this.width / 2 + 25 + 2, this.height / 2, 50, 20, "Z"));
        ButtonWidget currentPositionButton = new ButtonWidget(this.width / 2 - 100, this.height / 2 - 30, 200, 20, Text.of("Use Current Position"), (button) -> {
            setCoordinates(currentPosition.getX(), currentPosition.getY(), currentPosition.getZ());
        });
        this.addDrawableChild(currentPositionButton);
        this.addButton(confirmButton = new ButtonWidget(this.width / 2 - 202, this.height - 30, 200, 20, "Confirm and exit", (button) -> {
            PositionSetting newPosition = new PositionSetting(Integer.parseInt(xWidget.getText()), Integer.parseInt(yWidget.getText()), Integer.parseInt(zWidget.getText()));
            resultPromise.setSuccess(Optional.of(newPosition.toString()));
            closeScreen();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height - 30, 200, 20, "Cancel", (button) -> {
            resultPromise.setSuccess(Optional.empty());
            closeScreen();
        }));
        currentPositionButton.visible = currentPosition != null;
        setCoordinates(previousSelection.getX(), previousSelection.getY(), previousSelection.getZ());
    }

    private void setCoordinates(int x, int y, int z) {
        xWidget.setText(String.valueOf(x));
        yWidget.setText(String.valueOf(y));
        zWidget.setText(String.valueOf(z));
    }

    private void closeScreen() {
        onClose();
        MinecraftClient.getInstance().openScreen(parent);
    }

    @Override
    public void render(int mouseX, int mouseY, float delta) {
        this.renderBackground();
        if (confirmButton.active && hasInvalidCoordinate()) {
            confirmButton.active = false;
        } else if (!confirmButton.active && !hasInvalidCoordinate()) {
            confirmButton.active = true;
        }
        super.render(mouseX, mouseY, delta);
    }

    private boolean hasInvalidCoordinate() {
        return !IS_NUMBER.test(xWidget.getText())
            || !IS_NUMBER.test(yWidget.getText())
            || !IS_NUMBER.test(zWidget.getText());
    }
}
