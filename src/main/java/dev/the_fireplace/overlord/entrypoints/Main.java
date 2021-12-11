package dev.the_fireplace.overlord.entrypoints;

import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.entrypoints.DIModInitializer;
import dev.the_fireplace.overlord.Overlord;
import dev.the_fireplace.overlord.augment.Augments;
import dev.the_fireplace.overlord.block.OverlordBlocks;
import dev.the_fireplace.overlord.blockentity.OverlordBlockEntities;
import dev.the_fireplace.overlord.datapack.OverlordDataPacks;
import dev.the_fireplace.overlord.domain.registry.HeadBlockAugmentRegistry;
import dev.the_fireplace.overlord.entity.OverlordEntities;
import dev.the_fireplace.overlord.item.OverlordItems;
import dev.the_fireplace.overlord.network.server.OverlordServerPacketReceivers;

public final class Main implements DIModInitializer
{
    @Override
    public void onInitialize(Injector diContainer) {
        Overlord.getLogger().debug("Preparing bones...");
        OverlordBlocks.registerBlocks();
        OverlordItems.registerItems();
        OverlordBlockEntities.register();
        OverlordEntities.register();
        Augments.register(diContainer.getInstance(HeadBlockAugmentRegistry.class));
        diContainer.getInstance(OverlordServerPacketReceivers.class).registerPacketHandlers();
        OverlordDataPacks.register(diContainer);
    }
}
