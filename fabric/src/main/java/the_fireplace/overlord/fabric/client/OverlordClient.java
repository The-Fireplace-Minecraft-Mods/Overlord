package the_fireplace.overlord.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import the_fireplace.overlord.fabric.client.renderer.OwnedSkeletonRenderer;
import the_fireplace.overlord.fabric.init.OverlordEntities;

public class OverlordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(OverlordEntities.OWNED_SKELETON_TYPE, (erd, ctx) -> new OwnedSkeletonRenderer(erd));
    }
}
