package dev.the_fireplace.overlord.entrypoints;

import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.entrypoints.ClientDIModInitializer;
import dev.the_fireplace.lib.api.datagen.injectables.DataGeneratorFactory;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.client.gui.block.CasketGui;
import dev.the_fireplace.overlord.client.gui.entity.OwnedSkeletonGui;
import dev.the_fireplace.overlord.client.renderer.OwnedSkeletonRenderer;
import dev.the_fireplace.overlord.client.renderer.TombstoneBlockEntityRenderer;
import dev.the_fireplace.overlord.container.ContainerEquipmentSlot;
import dev.the_fireplace.overlord.datagen.*;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.network.client.ClientPacketRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.container.GenericContainer;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.player.PlayerInventory;

import java.io.IOException;
import java.nio.file.Paths;

@Environment(EnvType.CLIENT)
public final class Client implements ClientDIModInitializer
{
    @Override
    public void onInitializeClient(Injector diContainer) {
        registerEntityRenderers();
        registerBlockEntityRenderers();
        registerGuis();
        diContainer.getInstance(ClientPacketRegistry.class).registerPacketHandlers();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            Overlord.getLogger().debug("Generating data...");
            DataGenerator gen = diContainer.getInstance(DataGeneratorFactory.class).createAdditive(Paths.get("..", "src", "main", "resources"));
            gen.install(new BlockTagsProvider(gen));
            gen.install(new EntityTypeTagsProvider(gen));
            gen.install(new ItemTagsProvider(gen));
            gen.install(new RecipesProvider(gen));
            gen.install(new LootTablesProvider(gen));
            try {
                gen.run();
            } catch (IOException e) {
                Overlord.getLogger().error(e);
            }
        }
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.INSTANCE.register(OverlordEntities.OWNED_SKELETON_TYPE, (erd, ctx) -> new OwnedSkeletonRenderer(erd));
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.INSTANCE.register(OverlordBlockEntities.TOMBSTONE_BLOCK_ENTITY, TombstoneBlockEntityRenderer::new);
    }

    private void registerGuis() {
        ScreenProviderRegistry.INSTANCE.<GenericContainer>registerFactory(
            OverlordBlockEntities.CASKET_BLOCK_ENTITY_ID,
            (container) -> new CasketGui(container, getClientPlayerInventory())
        );
        ScreenProviderRegistry.INSTANCE.<OwnedSkeletonContainer>registerFactory(
            OverlordEntities.OWNED_SKELETON_ID,
            (container) -> new OwnedSkeletonGui(container.getOwner(), getClientPlayerInventory(), container.syncId)
        );
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> {
            registry.register(ContainerEquipmentSlot.EMPTY_WEAPON_SLOT_TEXTURE);
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
