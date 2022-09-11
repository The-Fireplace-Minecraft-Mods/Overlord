package dev.the_fireplace.overlord.entrypoints;

import com.google.inject.Injector;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.advancement.OverlordCriterions;
import dev.the_fireplace.overlord.augment.Augments;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.datapack.OverlordDataPacks;
import dev.the_fireplace.overlord.domain.registry.HeadBlockAugmentRegistry;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.item.OverlordItems;
import dev.the_fireplace.overlord.network.NetworkRegistry;
import net.fabricmc.api.ModInitializer;

public final class Main implements ModInitializer
{
    @Override
    public void onInitialize() {
        Injector injector = OverlordConstants.getInjector();
        OverlordConstants.getLogger().debug("Preparing bones...");
        injector.getInstance(OverlordBlocks.class).registerBlocks(OverlordBlocks.RegistryType.BOTH);
        injector.getInstance(OverlordItems.class).registerItems(false);
        injector.getInstance(OverlordBlockEntities.class).register();
        injector.getInstance(OverlordEntities.class).register();
        Augments.register(injector.getInstance(HeadBlockAugmentRegistry.class));
        injector.getInstance(NetworkRegistry.class).register();
        OverlordDataPacks.register(injector);
        OverlordCriterions.register();
    }
}
