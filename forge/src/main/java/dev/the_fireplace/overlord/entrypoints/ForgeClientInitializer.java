package dev.the_fireplace.overlord.entrypoints;

import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.client.advancement.ClientProgressFinder;
import dev.the_fireplace.overlord.client.gui.block.CasketGui;
import dev.the_fireplace.overlord.client.gui.entity.OwnedSkeletonGui;
import dev.the_fireplace.overlord.client.model.OverlordModelLayers;
import dev.the_fireplace.overlord.client.renderer.OwnedSkeletonRenderer;
import dev.the_fireplace.overlord.client.renderer.blockentity.ArmySkullBlockEntityRenderer;
import dev.the_fireplace.overlord.client.renderer.blockentity.TombstoneBlockEntityRenderer;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.entity.OwnedSkeletonContainer;
import dev.the_fireplace.overlord.impl.advancement.ProgressFinderProxies;
import dev.the_fireplace.overlord.item.OverlordItems;
import dev.the_fireplace.overlord.loader.ForgeCreativeTabHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import javax.inject.Inject;

public final class ForgeClientInitializer
{
    private final OverlordItems overlordItems;
    private final OverlordEntities overlordEntities;
    private final OverlordBlockEntities overlordBlockEntities;
    private final ForgeCreativeTabHelper forgeCreativeTabHelper;

    @Inject
    public ForgeClientInitializer(
        OverlordItems overlordItems,
        OverlordEntities overlordEntities,
        OverlordBlockEntities overlordBlockEntities,
        ForgeCreativeTabHelper forgeCreativeTabHelper
    ) {
        this.overlordItems = overlordItems;
        this.overlordEntities = overlordEntities;
        this.overlordBlockEntities = overlordBlockEntities;
        this.forgeCreativeTabHelper = forgeCreativeTabHelper;
    }

    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.LOW, this::registerScreens);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBlockEntityRenderers);
        FMLJavaModLoadingContext.get().getModEventBus().register(this.forgeCreativeTabHelper);
        OverlordModelLayers.register();
        ProgressFinderProxies.addFinder(LocalPlayer.class, new ClientProgressFinder());
    }

    private void registerItemRenderers() {
        //BuiltinItemRendererRegistry.INSTANCE.register(overlordItems.getFleshSkeletonSkull(), new ArmySkullItemRenderer());
        //BuiltinItemRendererRegistry.INSTANCE.register(overlordItems.getFleshMuscleSkeletonSkull(), new ArmySkullItemRenderer());
        //BuiltinItemRendererRegistry.INSTANCE.register(overlordItems.getMuscleSkeletonSkull(), new ArmySkullItemRenderer());
    }

    @SuppressWarnings("RedundantCast")
    public void registerScreens(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.MENU)) {
            return;
        }
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
}
