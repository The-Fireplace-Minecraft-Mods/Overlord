package dev.the_fireplace.overlord.loader;

import com.google.inject.Singleton;
import dev.the_fireplace.annotateddi.api.di.Implementation;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

@Singleton
@Implementation
public final class FabricCreativeTabHelper implements CreativeTabHelper
{
    public void registerItemAfter(ItemLike afterLast, ItemLike... items) {
        CreativeModeTabs.allTabs().stream()
            .filter(tab -> tab.contains(new ItemStack(afterLast)))
            .findFirst()
            .ifPresent(tab ->
                ItemGroupEvents.modifyEntriesEvent(tab).register(content -> {
                    content.addAfter(afterLast, items);
                })
            );
    }
}
