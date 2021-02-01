package the_fireplace.overlord;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OverlordHelper {
    public static final String MODID = "overlord";
    public static final Logger LOGGER = LogManager.getLogger("overlord");
    private static ILoaderHelper loaderHelper = null;

    public static void setLoaderHelper(ILoaderHelper helper) {
        if(loaderHelper == null)
            loaderHelper = helper;
    }

    public static ILoaderHelper getLoaderHelper() {
        return loaderHelper;
    }

    public static void errorWithStacktrace(String message, Object... args) {
        LOGGER.error(message, args);
        new Throwable().printStackTrace();
    }
}
