package dev.the_fireplace.overlord.entrypoints;

import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.item.OverlordItems;
import me.shedaniel.rei.api.EntryRegistry;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class ReiClient implements REIPluginV0
{
    @Override
    public void registerEntries(EntryRegistry registry) {
        //noinspection UnstableApiUsage
        registry.removeEntry(EntryStack.create(OverlordItems.SANS_MASK));
    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(Overlord.MODID, Overlord.MODID);
    }
}
