package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class SquadSelectorGui extends Screen
{
    private final Screen parent;
    private final Collection<? extends Squad> ownedSquads;
    @Nullable
    private final Integer entityId;
    @Nullable
    private final UUID currentSquad;

    public SquadSelectorGui(Text title, Screen parent, Collection<? extends Squad> ownedSquads, @Nullable Integer entityId, @Nullable UUID currentSquad) {
        super(title);
        this.parent = parent;
        this.currentSquad = currentSquad;
        this.entityId = entityId;
        this.ownedSquads = ownedSquads;
    }

    @Override
    protected void init() {
        SquadSelectorWidget squadSelectorWidget = createSquadSelector();
        this.addDrawableChild(squadSelectorWidget);
        //TODO select none?
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 202, this.height - 30, 200, 20, Text.of("Confirm and exit"), (button) -> {
            if (entityId != null) {
                //TODO send update packet
            }
            closeScreen();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 + 2, this.height - 30, 200, 20, Text.of("Cancel"), (button) -> {
            closeScreen();
        }));
    }

    private SquadSelectorWidget createSquadSelector() {
        SquadSelectorWidget squadSelectorWidget = new SquadSelectorWidget(this.client, this.width / 3, this.height - 34, 0, this.height - 34, 40);
        squadSelectorWidget.addSquads(ownedSquads);
        if (currentSquad != null) {
            squadSelectorWidget.selectSquad(currentSquad);
        }
        return squadSelectorWidget;
    }

    private void closeScreen() {
        MinecraftClient.getInstance().openScreen(parent);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        //TODO
        super.render(matrixStack, mouseX, mouseY, delta);
    }
}
