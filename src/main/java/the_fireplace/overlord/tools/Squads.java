package the_fireplace.overlord.tools;

import net.minecraftforge.common.DimensionManager;
import the_fireplace.overlord.Overlord;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class Squads implements Serializable {
    private static Squads instance = null;
    private static final String dataFileName = "overlordplayersquads.dat";
    private static File saveDir = DimensionManager.getCurrentSaveRootDirectory();

    private ArrayList<SquadData> squads;

    public static Squads getInstance(){
        return instance;
    }

    private Squads(){
        this.squads = new ArrayList<>();
        instance = this;
    }

    public ArrayList<SquadData> getSquads(){
        return squads;
    }

    public ArrayList<String> getSquadsFor(UUID player){
        ArrayList<String> allies = new ArrayList<>();
        for(SquadData data: squads){
            if(UUID.fromString(data.getUUID()).equals(player)) {
                allies = data.getSquads();
                break;
            }
        }
        return allies;
    }

    public void setPlayerSquadNames(UUID player, ArrayList<String> names) {
        for (SquadData data : squads) {
            if (data.getUUID().equals(player.toString())){
                data.setSquads(names);
                save();
                return;
            }
        }
        squads.add(new SquadData(player.toString(), names));
        save();
    }

    public static void save() {
        Overlord.logDebug("Squads saving...");
        saveToFile();
    }

    public static void load() {
        Overlord.logDebug("Squads loading...");
        readFromFile();
    }

    private static void readFromFile() {
        if(saveDir == null)
            saveDir = DimensionManager.getCurrentSaveRootDirectory();
        if(saveDir == null) {
            Overlord.logError("Could not get save directory. Squads will not load properly.");
            instance = new Squads();
            return;
        }
        File f = new File(saveDir, dataFileName);
        if (f.exists()) {
            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(f));
                instance = (Squads) stream.readObject();
                stream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                instance = new Squads();
                f.delete();
            }
        }
        if (instance == null)
            instance = new Squads();
    }

    private static void saveToFile() {
        try {
            if(saveDir == null)
                saveDir = DimensionManager.getCurrentSaveRootDirectory();
            if(saveDir == null)
                Overlord.logError("Could not get save directory. Squads will not save properly.");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(saveDir, dataFileName)));
            out.writeObject(instance);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
