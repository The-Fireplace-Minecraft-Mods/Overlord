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
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
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
