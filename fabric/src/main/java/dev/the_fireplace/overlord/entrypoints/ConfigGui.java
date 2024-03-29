package dev.the_fireplace.overlord.entrypoints;

import dev.the_fireplace.lib.api.client.entrypoints.ConfigGuiEntrypoint;
import dev.the_fireplace.lib.api.client.interfaces.ConfigGuiRegistry;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.config.OverlordConfigScreenFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ConfigGui implements ConfigGuiEntrypoint
{
    @Override
    public void registerConfigGuis(ConfigGuiRegistry configGuiRegistry) {
        configGuiRegistry.register(OverlordConstants.MODID, OverlordConstants.getInjector().getInstance(OverlordConfigScreenFactory.class)::getConfigScreen);
    }
}
