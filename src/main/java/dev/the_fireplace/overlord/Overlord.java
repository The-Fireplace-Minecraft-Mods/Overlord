package dev.the_fireplace.overlord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Overlord
{
    public static final String MODID = "overlord";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public static Logger getLogger() {
        return LOGGER;
    }
}
