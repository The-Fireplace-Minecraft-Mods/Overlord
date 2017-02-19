package the_fireplace.overlord.tools;

import net.minecraftforge.common.DimensionManager;
import the_fireplace.overlord.Overlord;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class Enemies implements Serializable {
    private static Enemies instance = null;
    private static final String dataFileName = "overlordenemies.dat";
    private static File saveDir = DimensionManager.getCurrentSaveRootDirectory();

    private ArrayList<Alliance> enemies;

    public static Enemies getInstance(){
        return instance;
    }

    private Enemies(){
        this.enemies = new ArrayList<>();
        instance = this;
    }

    public ArrayList<Alliance> getEnemies(){
        return enemies;
    }

    public boolean isEnemiesWith(UUID uuid1, UUID uuid2){
        for(Alliance playerPair: enemies){
            if((UUID.fromString(playerPair.getUser1().getUUID()).equals(uuid1) && UUID.fromString(playerPair.getUser2().getUUID()).equals(uuid2)) || (UUID.fromString(playerPair.getUser1().getUUID()).equals(uuid2) && UUID.fromString(playerPair.getUser2().getUUID()).equals(uuid1)))
                return true;
        }
        return false;
    }

    public boolean considersPlayerEnemy(UUID main, UUID other){
        for(Alliance playerPair: enemies){
            if((UUID.fromString(playerPair.getUser1().getUUID()).equals(main) && UUID.fromString(playerPair.getUser2().getUUID()).equals(other)))
                return true;
        }
        return false;
    }

    public ArrayList<StringPair> getAllEnemies(UUID player){
        ArrayList<StringPair> enemies = new ArrayList<>();
        for(Alliance playerPair: this.enemies){
            if(UUID.fromString(playerPair.getUser1().getUUID()).equals(player))
                enemies.add(playerPair.getUser2());
            else if(UUID.fromString(playerPair.getUser2().getUUID()).equals(player))
                enemies.add(playerPair.getUser1());
        }
        return enemies;
    }

    public ArrayList<StringPair> getMyEnemies(UUID player){
        ArrayList<StringPair> enemies = new ArrayList<>();
        for(Alliance playerPair: this.enemies){
            if(UUID.fromString(playerPair.getUser1().getUUID()).equals(player))
                enemies.add(playerPair.getUser2());
        }
        return enemies;
    }

    public ArrayList<StringPair> getWhoEnemied(UUID player){
        ArrayList<StringPair> enemies = new ArrayList<>();
        for(Alliance playerPair: this.enemies){
            if(UUID.fromString(playerPair.getUser2().getUUID()).equals(player))
                enemies.add(playerPair.getUser1());
        }
        return enemies;
    }

    public void addEnemies(Alliance playerPair) {
        for (Alliance pair1 : enemies) {
            if (pair1.equals(playerPair))
                return;
        }
        enemies.add(playerPair);
        save();
    }

    public void removeEnemies(Alliance playerPair) {
        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).equals(playerPair))
                enemies.remove(i);
        }
        save();
    }

    public static void save() {
        Overlord.logDebug("Enemies saving...");
        saveToFile();
    }

    public static void load() {
        Overlord.logDebug("Enemies loading...");
        readFromFile();
    }

    private static void readFromFile() {
        if(saveDir == null)
            saveDir = DimensionManager.getCurrentSaveRootDirectory();
        if(saveDir == null) {
            Overlord.logError("Could not get save directory. Enemies will not load properly.");
            instance = new Enemies();
            return;
        }
        File f = new File(saveDir, dataFileName);
        if (f.exists()) {
            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(f));
                instance = (Enemies) stream.readObject();
                stream.close();
            } catch (IOException|ClassNotFoundException e) {
                e.printStackTrace();
                instance = new Enemies();
                f.delete();
            }
        }
        if (instance == null)
            instance = new Enemies();
    }

    private static void saveToFile() {
        try {
            if(saveDir == null)
                saveDir = DimensionManager.getCurrentSaveRootDirectory();
            if(saveDir == null) {
                Overlord.logError("Could not get save directory. Enemies will not save properly.");
                return;
            }
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(saveDir, dataFileName)));
            out.writeObject(instance);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
