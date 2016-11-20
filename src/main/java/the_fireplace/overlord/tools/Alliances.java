package the_fireplace.overlord.tools;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.DimensionManager;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
@MethodsReturnNonnullByDefault
public class Alliances implements Serializable {
    private static Alliances instance = null;
    private static final String dataFileName = "overlordalliances.dat";
    private static final File saveDir = DimensionManager.getCurrentSaveRootDirectory();

    private ArrayList<Alliance> alliances;

    public static Alliances getInstance(){
        return instance;
    }

    private Alliances(){
        this.alliances = new ArrayList<>();
        instance = this;
    }

    public ArrayList<Alliance> getAlliances(){
        return alliances;
    }

    public boolean isAlliedTo(UUID uuid1, UUID uuid2){
        for(Alliance alliance:alliances){
            if((UUID.fromString(alliance.getUser1().getUUID()).equals(uuid1) && UUID.fromString(alliance.getUser2().getUUID()).equals(uuid2)) || (UUID.fromString(alliance.getUser1().getUUID()).equals(uuid2) && UUID.fromString(alliance.getUser2().getUUID()).equals(uuid1)))
                return true;
        }
        return false;
    }

    public ArrayList<StringPair> getAllies(UUID player){
        ArrayList<StringPair> allies = new ArrayList<>();
        for(Alliance alliance:alliances){
            if(UUID.fromString(alliance.getUser1().getUUID()).equals(player))
                allies.add(alliance.getUser2());
            else if(UUID.fromString(alliance.getUser2().getUUID()).equals(player))
                allies.add(alliance.getUser1());
        }
        return allies;
    }

    public void addAlliance(Alliance alliance) {
        for (Alliance alliance1 : alliances) {
            if (alliance1.equals(alliance))
                return;
        }
        alliances.add(alliance);
        save();
    }

    public void removeAlliance(Alliance alliance) {
        for (int i = 0; i < alliances.size(); i++) {
            if (alliances.get(i).equals(alliance))
                alliances.remove(i);
        }
        save();
    }

    public static void save() {
        saveToFile();
    }

    public static void load() {
        readFromFile();
    }

    @SuppressWarnings("TryWithIdenticalCatches")
    private static void readFromFile() {
        File f = new File(saveDir, dataFileName);
        if (f.exists()) {
            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(f));
                instance = (Alliances) stream.readObject();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                instance = new Alliances();
                f.delete();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                instance = new Alliances();
                f.delete();
            }
        }
        if (instance == null)
            instance = new Alliances();
    }

    private static void saveToFile() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(saveDir, dataFileName)));
            out.writeObject(instance);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
