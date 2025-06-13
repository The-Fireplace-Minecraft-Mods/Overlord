package dev.the_fireplace.overlord.util;

import java.util.UUID;

public class UUIDSerialization
{
    public static UUID storedUUIDtoUUID(String storedUUID) {
        return UUID.fromString(
            storedUUID
                .replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
                )
        );
    }
}
