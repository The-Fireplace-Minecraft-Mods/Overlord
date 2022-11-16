package dev.the_fireplace.overlord.loader;

import dev.the_fireplace.annotateddi.api.di.Implementation;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Implementation(environment = "CLIENT")
@Singleton
public final class ForgeModelLayerRegistry implements ModelLayerRegistry
{
    private final Map<ModelLayerLocation, Supplier<LayerDefinition>> layers = new HashMap<>();
    private boolean isListenerRegistered = false;

    @Override
    public void register(ModelLayerLocation modelLayerLocation, Supplier<LayerDefinition> layerSupplier) {
        this.layers.put(modelLayerLocation, layerSupplier);
        if (!isListenerRegistered) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelLayerRegistration);
            isListenerRegistered = true;
        }
    }

    public void onModelLayerRegistration(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>> entry : layers.entrySet()) {
            event.registerLayerDefinition(entry.getKey(), entry.getValue());
        }
    }
}
