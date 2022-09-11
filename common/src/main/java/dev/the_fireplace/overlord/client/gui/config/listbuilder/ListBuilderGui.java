package dev.the_fireplace.overlord.client.gui.config.listbuilder;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.the_fireplace.lib.api.client.interfaces.CustomButtonScreen;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.UUID;

public class ListBuilderGui extends Screen implements CustomButtonScreen<String>
{
    private final Promise<Optional<String>> resultPromise;
    private final Screen parent;
    private UUID selected;

    public ListBuilderGui(Component title, Screen parent, String currentValue) {
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
        ListSelectorWidget listSelectorWidget = createListSelector();
        this.addWidget(listSelectorWidget);
        this.addButton(new Button(this.width / 2 - 202, this.height - 30, 200, 20, Component.nullToEmpty("Confirm and exit"), (button) -> {
            onClose();
        }));
        this.addButton(new Button(this.width / 2 + 2, this.height - 30, 200, 20, Component.nullToEmpty("Cancel"), (button) -> {
            resultPromise.setSuccess(Optional.empty());
            onClose();
        }));
    }

    private ListSelectorWidget createListSelector() {
        ListSelectorWidget listSelectorWidget = new ListSelectorWidget(this.minecraft, this.width / 3, this.height - 34, 0, this.height - 34, 40);
        //TODO add entries
        return listSelectorWidget;
    }

    @Override
    public void onClose() {
        if (!resultPromise.isDone()) {
            resultPromise.setSuccess(Optional.of(selected.toString()));
        }
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        //TODO
        super.render(matrixStack, mouseX, mouseY, delta);
    }
}
