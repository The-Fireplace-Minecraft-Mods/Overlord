package the_fireplace.overlord.tools;

import net.minecraftforge.common.DimensionManager;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author The_Fireplace
 */
public class Enemies implements Serializable {
    private static Enemies instance = null;
    private static final String dataFileName = "overlordenemies.dat";
    private static final File saveDir = DimensionManager.getCurrentSaveRootDirectory();

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

    public boolean isNotEnemiesWith(UUID uuid1, UUID uuid2){
        for(Alliance playerPair: enemies){
            if((UUID.fromString(playerPair.getUser1().getUUID()).equals(uuid1) && UUID.fromString(playerPair.getUser2().getUUID()).equals(uuid2)) || (UUID.fromString(playerPair.getUser1().getUUID()).equals(uuid2) && UUID.fromString(playerPair.getUser2().getUUID()).equals(uuid1)))
                return false;
        }
        return true;
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
        ArrayList<StringPair> enemies = this.enemies.stream().filter(playerPair -> UUID.fromString(playerPair.getUser1().getUUID()).equals(player)).map(Alliance::getUser2).collect(Collectors.toCollection(ArrayList::new));
        return enemies;
    }

    public ArrayList<StringPair> getWhoEnemied(UUID player){
        ArrayList<StringPair> enemies = this.enemies.stream().filter(playerPair -> UUID.fromString(playerPair.getUser2().getUUID()).equals(player)).map(Alliance::getUser1).collect(Collectors.toCollection(ArrayList::new));
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
                instance = (Enemies) stream.readObject();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                instance = new Enemies();
                f.delete();
            } catch (ClassNotFoundException e) {
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
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(saveDir, dataFileName)));
            out.writeObject(instance);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
