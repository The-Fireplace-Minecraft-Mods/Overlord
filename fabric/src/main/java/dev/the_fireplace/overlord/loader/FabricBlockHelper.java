package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

@Implementation
public final class FabricBlockHelper implements BlockHelper
{
    @Override
    public BlockBehaviour.Properties copyProperties(BlockBehaviour block) {
        return FabricBlockSettings.copyOf(block);
    }

    @Override
    public BlockBehaviour.Properties createProperties(Material material) {
        return FabricBlockSettings.of(material);
    }
}
