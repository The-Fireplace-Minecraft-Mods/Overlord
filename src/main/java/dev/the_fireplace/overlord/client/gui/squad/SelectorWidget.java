package dev.the_fireplace.overlord.client.gui.squad;

import dev.the_fireplace.overlord.domain.data.objects.Squad;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class SelectorWidget extends AlwaysSelectedEntryListWidget<SelectorEntry>//TODO maybe this shouldn't be an always selected list? What if we want to deselect?
{
    public SelectorWidget(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight) {
        super(minecraftClient, width, height, top, bottom, itemHeight);
        this.setRenderBackground(false);
    }

    public void addSquads(Collection<? extends Squad> squads) {
        for (Squad squad : squads) {
            this.addEntry(new SelectorEntry(squad));
        }
    }

    public void selectSquad(UUID squadId) {
        Optional<SelectorEntry> firstMatchingSquad = this.children().stream().filter(entry -> entry.hasId(squadId)).findFirst();
        firstMatchingSquad.ifPresent(this::setSelected);
    }
}
