package dev.the_fireplace.overlord.client;

import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.entrypoints.ClientDIModInitializer;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.client.gui.CasketGui;
import dev.the_fireplace.overlord.client.gui.OwnedSkeletonGui;
import dev.the_fireplace.overlord.client.particle.DeadFlameParticle;
import dev.the_fireplace.overlord.client.particle.ScorchedFlameParticle;
import dev.the_fireplace.overlord.client.renderer.OwnedSkeletonRenderer;
import dev.the_fireplace.overlord.domain.internal.network.client.ClientPacketRegistry;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.init.OverlordBlockEntities;
import dev.the_fireplace.overlord.init.OverlordBlocks;
import dev.the_fireplace.overlord.init.OverlordEntities;
import dev.the_fireplace.overlord.init.OverlordParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class OverlordClient implements ClientDIModInitializer {
    @Override
    public void onInitializeClient(Injector diContainer) {
        registerEntityRenderers();
        registerGuis();
        registerParticles();
        diContainer.getInstance(ClientPacketRegistry.class).registerPacketHandlers();
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.INSTANCE.register(OverlordEntities.OWNED_SKELETON_TYPE, (erd, ctx) -> new OwnedSkeletonRenderer(erd));
    }

    private void registerParticles() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                OverlordBlocks.SCORCHED_TORCH,
                OverlordBlocks.TORCH_OF_THE_DEAD,
                OverlordBlocks.WALL_SCORCHED_TORCH,
                OverlordBlocks.WALL_TORCH_OF_THE_DEAD);
        ParticleFactoryRegistry.getInstance().register(OverlordParticleTypes.DEAD_FLAME, DeadFlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(OverlordParticleTypes.SCORCHED_FLAME, ScorchedFlameParticle.Factory::new);
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.PARTICLE_ATLAS_TEX).register((atlasTexture, registry) -> {
            registry.register(new Identifier(Overlord.MODID, "particle/dead_flame"));
            registry.register(new Identifier(Overlord.MODID, "particle/scorched_flame"));
        });
    }

    private void registerGuis() {
        ScreenProviderRegistry.INSTANCE.<GenericContainer>registerFactory(
            OverlordBlockEntities.CASKET_BLOCK_ENTITY_ID,
            (container) -> new CasketGui(container, getClientPlayerInventory())
        );
        ScreenProviderRegistry.INSTANCE.<OwnedSkeletonContainer>registerFactory(
            OverlordEntities.OWNED_SKELETON_ID,
            (container) -> new OwnedSkeletonGui(container.getOwner(), getClientPlayerInventory(), 0)
        );
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> {
            registry.register(OwnedSkeletonContainer.EMPTY_WEAPON_SLOT);
        });
    }

    private PlayerInventory getClientPlayerInventory() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            throw new IllegalStateException("Tried getting client player when it was null!");
        }
        return player.inventory;
    }
}
