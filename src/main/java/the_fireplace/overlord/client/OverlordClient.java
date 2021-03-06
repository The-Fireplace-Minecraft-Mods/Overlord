package the_fireplace.overlord.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.container.GenericContainer;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import the_fireplace.overlord.OverlordHelper;
import the_fireplace.overlord.client.gui.CasketGui;
import the_fireplace.overlord.client.gui.OwnedSkeletonGui;
import the_fireplace.overlord.client.particle.DeadFlameParticle;
import the_fireplace.overlord.client.particle.ScorchedFlameParticle;
import the_fireplace.overlord.client.renderer.OwnedSkeletonRenderer;
import the_fireplace.overlord.entity.OwnedSkeletonContainer;
import the_fireplace.overlord.init.OverlordBlockEntities;
import the_fireplace.overlord.init.OverlordBlocks;
import the_fireplace.overlord.init.OverlordEntities;
import the_fireplace.overlord.init.OverlordParticleTypes;

@Environment(EnvType.CLIENT)
public class OverlordClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(OverlordEntities.OWNED_SKELETON_TYPE, (erd, ctx) -> new OwnedSkeletonRenderer(erd));
        registerGuis();
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                OverlordBlocks.SCORCHED_TORCH,
                OverlordBlocks.TORCH_OF_THE_DEAD,
                OverlordBlocks.WALL_SCORCHED_TORCH,
                OverlordBlocks.WALL_TORCH_OF_THE_DEAD);
        ParticleFactoryRegistry.getInstance().register(OverlordParticleTypes.DEAD_FLAME, DeadFlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(OverlordParticleTypes.SCORCHED_FLAME, ScorchedFlameParticle.Factory::new);
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.PARTICLE_ATLAS_TEX).register((atlasTexture, registry) -> {
            registry.register(new Identifier(OverlordHelper.MODID, "particle/dead_flame"));
            registry.register(new Identifier(OverlordHelper.MODID, "particle/scorched_flame"));
        });
    }

    private void registerGuis() {
        assert MinecraftClient.getInstance().player != null;
        ScreenProviderRegistry.INSTANCE.<GenericContainer>registerFactory(OverlordBlockEntities.CASKET_BLOCK_ENTITY_ID,
                (container) -> new CasketGui(container, MinecraftClient.getInstance().player.inventory, new TranslatableText("container.casket")));
        ScreenProviderRegistry.INSTANCE.<OwnedSkeletonContainer>registerFactory(OverlordEntities.OWNED_SKELETON_ID,
                (container) -> new OwnedSkeletonGui(container.getOwner(), 0));
    }
}
