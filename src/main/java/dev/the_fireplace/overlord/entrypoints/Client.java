package dev.the_fireplace.overlord.entrypoints;

import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.entrypoints.ClientDIModInitializer;
import dev.the_fireplace.lib.api.datagen.injectables.DataGeneratorFactory;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.client.gui.CasketGui;
import dev.the_fireplace.overlord.client.gui.OwnedSkeletonGui;
import dev.the_fireplace.overlord.client.renderer.OwnedSkeletonRenderer;
import dev.the_fireplace.overlord.client.renderer.TombstoneBlockEntityRenderer;
import dev.the_fireplace.overlord.container.ContainerEquipmentSlot;
import dev.the_fireplace.overlord.datagen.*;
import dev.the_fireplace.overlord.domain.network.client.ClientPacketRegistry;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.data.DataGenerator;
import net.minecraft.screen.GenericContainerScreenHandler;

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
            BlockTagsProvider blockTagsProvider = new BlockTagsProvider(gen);
            gen.install(blockTagsProvider);
            gen.install(new EntityTypeTagsProvider(gen));
            gen.install(new ItemTagsProvider(gen, blockTagsProvider));
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

    @SuppressWarnings("RedundantTypeArguments")
    private void registerGuis() {
        ScreenRegistry.<GenericContainerScreenHandler, CasketGui>register(
            OverlordBlockEntities.CASKET_SCREEN_HANDLER,
            (container, playerInventory, title) -> new CasketGui(container, playerInventory)
        );
        ScreenRegistry.<OwnedSkeletonContainer, OwnedSkeletonGui>register(
            OverlordEntities.OWNED_SKELETON_SCREEN_HANDLER,
            (container, playerInventory, title) -> new OwnedSkeletonGui(container.getOwner(), playerInventory, container.syncId)
        );
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(ContainerEquipmentSlot.EMPTY_WEAPON_SLOT_TEXTURE);
        });
    }
}
