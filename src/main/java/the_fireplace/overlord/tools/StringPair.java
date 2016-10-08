package the_fireplace.overlord.tools;

import java.io.Serializable;

/**
 * Simple, serializable String Pairing system with 2 String variables.
 * @author MRebhan
 * @author The_Fireplace
 */

public class StringPair implements Serializable {
    private String player_uuid;
    private String player_name;

    public StringPair(String uuid, String name) {
        this.player_uuid = uuid;
        this.player_name = name;
    }

    public String getUUID() {
        return this.player_uuid;
    }

    public String getPlayerName() {
        return this.player_name;
    }

    @Override public String toString() {
        return StringPair.class.getName() + '@' + Integer.toHexString(this.hashCode()) + " [" + this.player_uuid + ", " + this.player_name + ']';
    }
}