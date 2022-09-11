package dev.the_fireplace.overlord.domain.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public interface HeadBlockAugmentRegistry
{
    void register(Block block, ResourceLocation augment);

    @Nullable
    ResourceLocation get(Block block);

    boolean has(Block block);
}
