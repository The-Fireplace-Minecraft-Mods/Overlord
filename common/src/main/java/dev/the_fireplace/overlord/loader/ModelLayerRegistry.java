package dev.the_fireplace.overlord.loader;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Supplier;

public interface ModelLayerRegistry
{
    void register(ModelLayerLocation modelLayerLocation, Supplier<LayerDefinition> layerSupplier);
}
