package dev.the_fireplace.overlord.client.gui.config.listbuilder;

import dev.the_fireplace.lib.api.client.interfaces.CustomButtonScreen;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ListBuilderGui extends Screen implements CustomButtonScreen<String>
{
    private final Promise<Optional<String>> resultPromise;
    private final Screen parent;
    private UUID selected;

    public ListBuilderGui(Text title, Screen parent, String currentValue) {
        super(title);
        this.resultPromise = new DefaultPromise<>(new DefaultEventExecutor());
        this.parent = parent;
        this.selected = UUID.fromString(currentValue);
    }

    @Override
    public Promise<Optional<String>> getNewValuePromise() {
        return resultPromise;
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 202, this.height - 30, 200, 20, Text.of("Confirm and exit"), (button) -> {
            closeScreen();
        }));
        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height - 30, 200, 20, Text.of("Cancel"), (button) -> {
            resultPromise.setSuccess(Optional.empty());
            closeScreen();
        }));
    }

    private void closeScreen() {
        onClose();
        MinecraftClient.getInstance().openScreen(parent);
    }

    @Override
    public void onClose() {
        if (!resultPromise.isDone()) {
            resultPromise.setSuccess(Optional.of(selected.toString()));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        //TODO
        super.render(matrixStack, mouseX, mouseY, delta);
    }
}
