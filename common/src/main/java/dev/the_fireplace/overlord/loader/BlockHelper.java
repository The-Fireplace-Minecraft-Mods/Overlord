package dev.the_fireplace.overlord.loader;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public interface BlockHelper
{
    BlockBehaviour.Properties copyProperties(BlockBehaviour block);

    BlockBehaviour.Properties createProperties(Material material);
}
