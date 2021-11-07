package dev.the_fireplace.overlord.client.gui.config.listbuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

@Environment(EnvType.CLIENT)
public class ListSelectorWidget extends AlwaysSelectedEntryListWidget<ListSelectorEntry>
{
    public ListSelectorWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }
}
