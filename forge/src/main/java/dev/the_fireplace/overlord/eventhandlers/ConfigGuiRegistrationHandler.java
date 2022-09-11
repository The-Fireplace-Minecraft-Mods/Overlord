package dev.the_fireplace.overlord.eventhandlers;

import dev.the_fireplace.lib.api.events.ConfigScreenRegistration;
import dev.the_fireplace.overlord.OverlordConstants;
import dev.the_fireplace.overlord.config.OverlordConfigScreenFactory;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.inject.Inject;

public final class ConfigGuiRegistrationHandler
{
    private final OverlordConfigScreenFactory configScreenFactory;

    @Inject
    public ConfigGuiRegistrationHandler(OverlordConfigScreenFactory configScreenFactory) {
        this.configScreenFactory = configScreenFactory;
    }

    @SubscribeEvent
    public void registerConfigGui(ConfigScreenRegistration configScreenRegistration) {
        configScreenRegistration.getConfigGuiRegistry().register(OverlordConstants.MODID, configScreenFactory::getConfigScreen);
    }
}