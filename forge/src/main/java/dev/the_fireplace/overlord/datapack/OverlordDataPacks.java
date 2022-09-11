package dev.the_fireplace.overlord.datapack;

import com.google.inject.Inject;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class OverlordDataPacks
{
    private final SkeletonBuildingReloadListener reloadListener;

    @Inject
    public OverlordDataPacks(SkeletonBuildingReloadListener reloadListener) {
        this.reloadListener = reloadListener;
    }

    @SubscribeEvent
    public void onResourceManagerReload(AddReloadListenerEvent event) {
        event.addListener(reloadListener);
    }
}
