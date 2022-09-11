package dev.the_fireplace.overlord;

import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.Injectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class OverlordConstants
{
    public static final String MODID = "overlord";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public static Logger getLogger() {
        return LOGGER;
    }

    public static Injector getInjector() {
        return Injectors.INSTANCE.getAutoInjector(MODID);
    }
}
