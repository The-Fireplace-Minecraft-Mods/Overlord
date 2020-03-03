package the_fireplace.overlord.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.GenericContainer;
import net.minecraft.text.TranslatableText;
import the_fireplace.overlord.fabric.client.gui.CasketGui;
import the_fireplace.overlord.fabric.client.gui.OwnedSkeletonGui;
import the_fireplace.overlord.fabric.client.renderer.OwnedSkeletonRenderer;
import the_fireplace.overlord.fabric.entity.OwnedSkeletonContainer;
import the_fireplace.overlord.fabric.init.OverlordBlockEntities;
import the_fireplace.overlord.fabric.init.OverlordEntities;

@Environment(EnvType.CLIENT)
public class OverlordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(OverlordEntities.OWNED_SKELETON_TYPE, (erd, ctx) -> new OwnedSkeletonRenderer(erd));
        registerGuis();
    }

    private void registerGuis() {
        assert MinecraftClient.getInstance().player != null;
        ScreenProviderRegistry.INSTANCE.<GenericContainer>registerFactory(OverlordBlockEntities.CASKET_BLOCK_ENTITY_ID,
                (container) -> new CasketGui(container, MinecraftClient.getInstance().player.inventory, new TranslatableText("container.casket")));
        ScreenProviderRegistry.INSTANCE.<OwnedSkeletonContainer>registerFactory(OverlordEntities.OWNED_SKELETON_ID,
                (container) -> new OwnedSkeletonGui(container.getOwner()));
    }
}
