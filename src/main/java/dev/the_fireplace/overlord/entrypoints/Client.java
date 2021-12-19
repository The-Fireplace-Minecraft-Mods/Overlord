package dev.the_fireplace.overlord.entrypoints;

import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.entrypoints.ClientDIModInitializer;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.client.advancement.ClientProgressFinder;
import dev.the_fireplace.overlord.client.gui.block.CasketGui;
import dev.the_fireplace.overlord.client.gui.entity.OwnedSkeletonGui;
import dev.the_fireplace.overlord.client.renderer.OwnedSkeletonRenderer;
import dev.the_fireplace.overlord.client.renderer.blockentity.ArmySkullBlockEntityRenderer;
import dev.the_fireplace.overlord.client.renderer.blockentity.TombstoneBlockEntityRenderer;
import dev.the_fireplace.overlord.client.renderer.item.ArmySkullItemRenderer;
import dev.the_fireplace.overlord.container.ContainerEquipmentSlot;
import dev.the_fireplace.overlord.datagen.OverlordDataGeneratorRunner;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.impl.advancement.ProgressFinderProxies;
import dev.the_fireplace.overlord.item.OverlordItems;
import dev.the_fireplace.overlord.network.client.OverlordClientPacketReceivers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.screen.GenericContainerScreenHandler;

@Environment(EnvType.CLIENT)
public final class Client implements ClientDIModInitializer
{
    @Override
    public void onInitializeClient(Injector diContainer) {
        registerEntityRenderers();
        registerBlockEntityRenderers();
        registerGuis();
        diContainer.getInstance(OverlordClientPacketReceivers.class).registerPacketHandlers();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            diContainer.getInstance(OverlordDataGeneratorRunner.class).run();
        }
        ProgressFinderProxies.addFinder(ClientPlayerEntity.class, new ClientProgressFinder());
    }

    private void registerEntityRenderers() {
        EntityRendererRegistry.INSTANCE.register(OverlordEntities.OWNED_SKELETON_TYPE, (erd, ctx) -> new OwnedSkeletonRenderer(erd));
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.INSTANCE.register(OverlordBlockEntities.TOMBSTONE_BLOCK_ENTITY, TombstoneBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(OverlordBlockEntities.ARMY_SKULL_BLOCK_ENTITY, ArmySkullBlockEntityRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(OverlordItems.FLESH_SKELETON_SKULL, new ArmySkullItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(OverlordItems.FLESH_MUSCLE_SKELETON_SKULL, new ArmySkullItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(OverlordItems.MUSCLE_SKELETON_SKULL, new ArmySkullItemRenderer());
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
