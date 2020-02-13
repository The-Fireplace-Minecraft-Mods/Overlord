package the_fireplace.overlord.fabric;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import the_fireplace.overlord.fabric.init.OverlordBlocks;
import the_fireplace.overlord.fabric.init.OverlordItems;

public class Overlord implements ModInitializer {
    public static final String MODID = "overlord";

    public static final Logger LOGGER = LogManager.getLogger("overlord");

    @Override
    public void onInitialize() {
        LOGGER.debug("Preparing bones...");
        OverlordBlocks.registerBlocks();
        OverlordItems.registerItems();
    }
}
