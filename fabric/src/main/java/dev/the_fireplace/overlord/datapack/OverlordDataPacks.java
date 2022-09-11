package dev.the_fireplace.overlord.datapack;

import com.google.inject.Injector;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public final class OverlordDataPacks
{
    public static void register(Injector injector) {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(injector.getInstance(SkeletonBuildingPackLoader.class));
    }
}
