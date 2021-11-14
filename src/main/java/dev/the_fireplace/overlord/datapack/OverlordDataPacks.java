package dev.the_fireplace.overlord.datapack;

import com.google.inject.Injector;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public final class OverlordDataPacks
{
    public static void register(Injector diContainer) {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(diContainer.getInstance(SkeletonBuildingPackLoader.class));
    }
}
