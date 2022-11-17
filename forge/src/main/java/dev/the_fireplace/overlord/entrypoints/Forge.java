package dev.the_fireplace.overlord.entrypoints;

import com.google.inject.Injector;
import dev.the_fireplace.lib.api.chat.injectables.TranslatorFactory;
import dev.the_fireplace.lib.api.events.FLEventBus;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.advancement.OverlordCriterions;
import dev.the_fireplace.overlord.augment.Augments;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.datapack.OverlordDataPacks;
import dev.the_fireplace.overlord.domain.registry.HeadBlockAugmentRegistry;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.eventhandlers.ConfigGuiRegistrationHandler;
import dev.the_fireplace.overlord.item.OverlordItems;
import dev.the_fireplace.overlord.loader.RegistryHelper;
import dev.the_fireplace.overlord.network.NetworkRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;

@Mod("overlord")
public final class Forge
{
    public Forge() {
        Injector injector = OverlordConstants.getInjector();
        injector.getInstance(TranslatorFactory.class).addTranslator(OverlordConstants.MODID);

        OverlordConstants.getLogger().debug("Preparing bones...");
        Augments.register(injector.getInstance(HeadBlockAugmentRegistry.class));
        injector.getInstance(NetworkRegistry.class).register();
        MinecraftForge.EVENT_BUS.register(injector.getInstance(OverlordDataPacks.class));
        OverlordCriterions.register();

        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
            FLEventBus.BUS.register(injector.getInstance(ConfigGuiRegistrationHandler.class));
            injector.getInstance(ForgeClientInitializer.class).init();
            return null;
        });
    }

    @Mod.EventBusSubscriber(modid = "overlord", bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class RegistryHandler
    {
        @SubscribeEvent
        public static void registerBlocks(RegisterEvent event) {
            if (!event.getRegistryKey().equals(Registry.BLOCK_REGISTRY)) {
                return;
            }
            OverlordBlocks overlordBlocks = OverlordConstants.getInjector().getInstance(OverlordBlocks.class);
            overlordBlocks.setBlockRegistry((id, value) -> event.register(Registry.BLOCK_REGISTRY, id, () -> value));
            overlordBlocks.registerBlocks(OverlordBlocks.RegistryType.BLOCK);
        }

        @SubscribeEvent
        public static void registerItems(RegisterEvent event) {
            if (!event.getRegistryKey().equals(Registry.ITEM_REGISTRY)) {
                return;
            }
            OverlordBlocks overlordBlocks = OverlordConstants.getInjector().getInstance(OverlordBlocks.class);
            OverlordItems overlordItems = OverlordConstants.getInjector().getInstance(OverlordItems.class);
            RegistryHelper<Item> itemRegistryHelper = (id, value) -> event.register(Registry.ITEM_REGISTRY, id, () -> value);
            overlordBlocks.setItemRegistry(itemRegistryHelper);
            overlordBlocks.registerBlocks(OverlordBlocks.RegistryType.ITEM);
            overlordItems.setItemRegistry(itemRegistryHelper);
            overlordItems.registerItems(true);
        }

        @SubscribeEvent
        public static void registerBlockEntities(RegisterEvent event) {
            if (!event.getRegistryKey().equals(Registry.BLOCK_ENTITY_TYPE_REGISTRY)) {
                return;
            }
            OverlordBlockEntities overlordBlockEntities = OverlordConstants.getInjector().getInstance(OverlordBlockEntities.class);
            overlordBlockEntities.setBlockEntityRegistry((id, value) -> event.register(Registry.BLOCK_ENTITY_TYPE_REGISTRY, id, () -> value));
            overlordBlockEntities.register();
        }

        @SubscribeEvent
        public static void registerEntities(RegisterEvent event) {
            if (!event.getRegistryKey().equals(Registry.ENTITY_TYPE_REGISTRY)) {
                return;
            }
            OverlordEntities overlordEntities = OverlordConstants.getInjector().getInstance(OverlordEntities.class);
            overlordEntities.setEntityRegistry((id, value) -> event.register(Registry.ENTITY_TYPE_REGISTRY, id, () -> value));
            overlordEntities.register();
        }
    }
}