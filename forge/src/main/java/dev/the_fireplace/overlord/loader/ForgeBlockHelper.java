package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

@Implementation
public final class ForgeBlockHelper implements BlockHelper
{
    @Override
    public BlockBehaviour.Properties copyProperties(BlockBehaviour block) {
        return BlockBehaviour.Properties.copy(block);
    }

    @Override
    public BlockBehaviour.Properties createProperties(Material material) {
        return BlockBehaviour.Properties.of(material);
    }
}
