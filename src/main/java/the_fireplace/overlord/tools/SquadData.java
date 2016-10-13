package the_fireplace.overlord.tools;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Simple, serializable String-ArrayList<String> Pairing system.
 * @author MRebhan
 * @author The_Fireplace
 */

public class SquadData implements Serializable {
    private String player_uuid;
    private ArrayList<String> squad_names;

    public SquadData(String uuid, ArrayList<String> name) {
        this.player_uuid = uuid;
        this.squad_names = name;
    }

    public String getUUID() {
        return this.player_uuid;
    }

    public ArrayList<String> getSquads() {
        return this.squad_names;
    }

    public void setSquads(ArrayList<String> squads){
        squad_names = squads;
    }

    @Override public String toString() {
        return SquadData.class.getName() + '@' + Integer.toHexString(this.hashCode()) + " [" + this.player_uuid + ", " + this.squad_names + ']';
    }
}