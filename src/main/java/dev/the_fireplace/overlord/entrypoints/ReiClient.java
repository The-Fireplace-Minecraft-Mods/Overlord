package dev.the_fireplace.overlord.entrypoints;

import dev.the_fireplace.overlord.item.OverlordItems;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ReiClient implements REIClientPlugin
{
    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.removeEntry(EntryStacks.of(OverlordItems.SANS_MASK));
    }
}
