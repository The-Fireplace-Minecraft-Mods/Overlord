package dev.the_fireplace.overlord.loader;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public interface BlockHelper
{
    Block.Properties copyProperties(Block block);

    Block.Properties createProperties(Material material);
}
