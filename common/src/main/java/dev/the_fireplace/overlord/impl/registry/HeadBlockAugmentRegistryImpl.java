package dev.the_fireplace.overlord.impl.registry;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import dev.the_fireplace.overlord.domain.registry.HeadBlockAugmentRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Implementation
@Singleton
public final class HeadBlockAugmentRegistryImpl implements HeadBlockAugmentRegistry
{
    private final Map<Block, ResourceLocation> blockAugments = new ConcurrentHashMap<>();

    @Override
    public void register(Block block, ResourceLocation augment) {
        blockAugments.put(block, augment);
    }

    @Nullable
    @Override
    public ResourceLocation get(Block block) {
        return blockAugments.get(block);
    }

    @Override
    public boolean has(Block block) {
        return blockAugments.containsKey(block);
    }
}
