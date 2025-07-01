package dev.the_fireplace.overlord.loader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Singleton;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.OverlordConstants;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Implementation
public final class ForgeCreativeTabHelper implements CreativeTabHelper
{
    private final Map<CreativeModeTab, Multimap<ItemLike, ItemLike>> itemAfters = new HashMap<>();

    @Override
    public void registerItemAfter(CreativeModeTab tab, ItemLike afterLast, ItemLike... items) {
        OverlordConstants.getLogger().debug("[Forge] Prefilling items to load to creative tab {}", tab.getDisplayName().getString());
        itemAfters.computeIfAbsent(tab, ignored -> ArrayListMultimap.create()).putAll(afterLast, Arrays.asList(items));
    }

    @SubscribeEvent
    public void registerItemAfter(CreativeModeTabEvent.BuildContents event) {
        OverlordConstants.getLogger().debug("[Forge] Prefilling items to creative tab {}", event.getTab().getDisplayName().getString());
        itemAfters.getOrDefault(event.getTab(), ArrayListMultimap.create()).forEach((afterLast, item) -> {
            event.accept(() -> item);
        });
    }
}
