package dev.the_fireplace.overlord.domain.registry;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

public interface HeadBlockAugmentRegistry
{
    void register(Block block, Identifier augment);

    @Nullable
    Identifier get(Block block);

    boolean has(Block block);
}
