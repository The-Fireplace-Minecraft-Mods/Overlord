package the_fireplace.overlord;

public class OverlordHelper {
    private static ILoaderHelper loaderHelper = null;

    public static void setLoaderHelper(ILoaderHelper helper) {
        if(loaderHelper == null)
            loaderHelper = helper;
    }

    public static ILoaderHelper getLoaderHelper() {
        return loaderHelper;
    }
}
