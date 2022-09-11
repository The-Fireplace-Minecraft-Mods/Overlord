package dev.the_fireplace.overlord.entrypoints;

import com.google.inject.Injector;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.client.advancement.ClientProgressFinder;
import dev.the_fireplace.overlord.client.gui.block.CasketGui;
import dev.the_fireplace.overlord.client.gui.entity.OwnedSkeletonGui;
import dev.the_fireplace.overlord.client.model.OverlordModelLayers;
import dev.the_fireplace.overlord.client.renderer.OwnedSkeletonRenderer;
import dev.the_fireplace.overlord.client.renderer.blockentity.ArmySkullBlockEntityRenderer;
import dev.the_fireplace.overlord.client.renderer.blockentity.TombstoneBlockEntityRenderer;
import dev.the_fireplace.overlord.client.renderer.item.ArmySkullItemRenderer;
import dev.the_fireplace.overlord.container.ContainerEquipmentSlot;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.impl.advancement.ProgressFinderProxies;
import dev.the_fireplace.overlord.item.OverlordItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.inventory.ChestMenu;

@Environment(EnvType.CLIENT)
public final class Client implements ClientModInitializer
{
    @Override
    public void onInitializeClient() {
        Injector injector = OverlordConstants.getInjector();
        OverlordItems overlordItems = injector.getInstance(OverlordItems.class);
        OverlordEntities overlordEntities = injector.getInstance(OverlordEntities.class);
        OverlordBlockEntities overlordBlockEntities = injector.getInstance(OverlordBlockEntities.class);
        registerEntityRenderers(overlordEntities);
        registerBlockEntityRenderers(overlordBlockEntities, overlordItems);
        registerGuis(overlordEntities, overlordBlockEntities);
        ProgressFinderProxies.addFinder(LocalPlayer.class, new ClientProgressFinder());
    }

    private void registerEntityRenderers(OverlordEntities overlordEntities) {
        OverlordModelLayers.register();
        EntityRendererRegistry.register(overlordEntities.getOwnedSkeletonType(), OwnedSkeletonRenderer::new);
    }

    private void registerBlockEntityRenderers(OverlordBlockEntities overlordBlockEntities, OverlordItems overlordItems) {
        BlockEntityRendererRegistry.register(overlordBlockEntities.getTombstoneBlockEntityType(), context -> new TombstoneBlockEntityRenderer());
        BlockEntityRendererRegistry.register(overlordBlockEntities.getArmySkullBlockEntityType(), ArmySkullBlockEntityRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register(overlordItems.getFleshSkeletonSkull(), new ArmySkullItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(overlordItems.getFleshMuscleSkeletonSkull(), new ArmySkullItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(overlordItems.getMuscleSkeletonSkull(), new ArmySkullItemRenderer());
    }

    @SuppressWarnings("RedundantTypeArguments")
    private void registerGuis(OverlordEntities overlordEntities, OverlordBlockEntities overlordBlockEntities) {
        ScreenRegistry.<ChestMenu, CasketGui>register(
            overlordBlockEntities.getCasketScreenHandler(),
            (container, playerInventory, title) -> new CasketGui(container, playerInventory)
        );
        ScreenRegistry.<OwnedSkeletonContainer, OwnedSkeletonGui>register(
            overlordEntities.getOwnedSkeletonScreenHandler(),
            (container, playerInventory, title) -> new OwnedSkeletonGui(container.getOwner(), playerInventory, container.containerId)
        );
        ClientSpriteRegistryCallback.event(TextureAtlas.LOCATION_BLOCKS).register((atlasTexture, registry) -> {
            registry.register(ContainerEquipmentSlot.EMPTY_WEAPON_SLOT_TEXTURE);
        });
    }
}
