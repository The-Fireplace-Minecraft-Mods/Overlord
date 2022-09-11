package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Supplier;

@Implementation(environment = "CLIENT")
public final class FabricModelLayerRegistry implements ModelLayerRegistry
{
    @Override
    public void register(ModelLayerLocation modelLayerLocation, Supplier<LayerDefinition> layerSupplier) {
        EntityModelLayerRegistry.registerModelLayer(modelLayerLocation, layerSupplier::get);
    }
}
