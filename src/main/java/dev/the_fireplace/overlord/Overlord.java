package dev.the_fireplace.overlord;

import com.google.inject.Injector;
import dev.the_fireplace.annotateddi.api.entrypoints.DIModInitializer;
import dev.the_fireplace.overlord.domain.network.server.ServerPacketRegistry;
import dev.the_fireplace.overlord.init.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Overlord implements DIModInitializer {
    public static final String MODID = "overlord";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public static Logger getLogger() {
        return LOGGER;
    }

    public static void errorWithStacktrace(String message, Object... args) {
        getLogger().error(String.format(message, args), new Throwable());
    }

    @Override
    public void onInitialize(Injector diContainer) {
        getLogger().debug("Preparing bones...");
        OverlordBlocks.registerBlocks();
        OverlordItems.registerItems();
        OverlordBlockEntities.register();
        OverlordEntities.register();
        OverlordParticleTypes.register();
        diContainer.getInstance(ServerPacketRegistry.class).registerPacketHandlers();
    }
}
