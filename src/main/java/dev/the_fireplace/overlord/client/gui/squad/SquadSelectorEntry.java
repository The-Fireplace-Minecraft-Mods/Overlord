package dev.the_fireplace.overlord.client.gui.squad;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class SquadSelectorEntry extends AlwaysSelectedEntryListWidget.Entry<SquadSelectorEntry>
{
    protected final MinecraftClient client;
    protected final Squad squad;

    SquadSelectorEntry(Squad squad) {
        this.client = MinecraftClient.getInstance();
        this.squad = squad;
    }

    @Override
    public void render(MatrixStack matrixStack, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        String name = this.squad.getName();
        String trimmedName = name.trim();
        int maxNameWidth = x - 3;
        TextRenderer font = this.client.textRenderer;
        if (font.getWidth(trimmedName) > maxNameWidth) {
            trimmedName = font.trimToWidth(trimmedName, maxNameWidth - font.getWidth("...")) + "...";
        }
        font.draw(matrixStack, trimmedName, y + 32 + 3, index + 1, 0xFFFFFF);
    }

    @Override
    public Text getNarration() {
        return Text.of(this.squad.getName());
    }

    public boolean hasId(UUID squadId) {
        return squad.getSquadId().equals(squadId);
    }
}
