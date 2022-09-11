package dev.the_fireplace.overlord.entrypoints;

import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.client.advancement.ClientProgressFinder;
import dev.the_fireplace.overlord.client.gui.block.CasketGui;
import dev.the_fireplace.overlord.client.gui.entity.OwnedSkeletonGui;
import dev.the_fireplace.overlord.client.model.OverlordModelLayers;
import dev.the_fireplace.overlord.client.renderer.OwnedSkeletonRenderer;
import dev.the_fireplace.overlord.client.renderer.blockentity.ArmySkullBlockEntityRenderer;
import dev.the_fireplace.overlord.client.renderer.blockentity.TombstoneBlockEntityRenderer;
import dev.the_fireplace.overlord.container.ContainerEquipmentSlot;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.impl.advancement.ProgressFinderProxies;
import dev.the_fireplace.overlord.item.OverlordItems;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.inject.Inject;

public final class ForgeClientInitializer
{
    private final OverlordItems overlordItems;
    private final OverlordEntities overlordEntities;
    private final OverlordBlockEntities overlordBlockEntities;

    @Inject
    public ForgeClientInitializer(OverlordItems overlordItems, OverlordEntities overlordEntities, OverlordBlockEntities overlordBlockEntities) {
        this.overlordItems = overlordItems;
        this.overlordEntities = overlordEntities;
        this.overlordBlockEntities = overlordBlockEntities;
    }

    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(MenuType.class, EventPriority.LOW, this::registerScreens);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBlockEntityRenderers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerSprite);
        OverlordModelLayers.register();
        ProgressFinderProxies.addFinder(LocalPlayer.class, new ClientProgressFinder());
    }

    private void registerItemRenderers() {
        //BuiltinItemRendererRegistry.INSTANCE.register(overlordItems.getFleshSkeletonSkull(), new ArmySkullItemRenderer());
        //BuiltinItemRendererRegistry.INSTANCE.register(overlordItems.getFleshMuscleSkeletonSkull(), new ArmySkullItemRenderer());
        //BuiltinItemRendererRegistry.INSTANCE.register(overlordItems.getMuscleSkeletonSkull(), new ArmySkullItemRenderer());
    }

    @SuppressWarnings("RedundantCast")
    public void registerScreens(RegistryEvent.Register<MenuType<?>> event) {
        MenuScreens.register(
            overlordBlockEntities.getCasketScreenHandler(),
            (MenuScreens.ScreenConstructor<ChestMenu, CasketGui>) (container, playerInventory, title) -> new CasketGui(container, playerInventory)
        );
        MenuScreens.register(
            overlordEntities.getOwnedSkeletonScreenHandler(),
            (MenuScreens.ScreenConstructor<OwnedSkeletonContainer, OwnedSkeletonGui>) (container, playerInventory, title) -> new OwnedSkeletonGui(container.getOwner(), playerInventory, container.containerId)
        );
    }

    public void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(overlordEntities.getOwnedSkeletonType(), OwnedSkeletonRenderer::new);
        event.registerBlockEntityRenderer(overlordBlockEntities.getTombstoneBlockEntityType(), context -> new TombstoneBlockEntityRenderer());
        event.registerBlockEntityRenderer(overlordBlockEntities.getArmySkullBlockEntityType(), ArmySkullBlockEntityRenderer::new);
    }

    public void registerSprite(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            event.addSprite(ContainerEquipmentSlot.EMPTY_WEAPON_SLOT_TEXTURE);
        }
    }
}
