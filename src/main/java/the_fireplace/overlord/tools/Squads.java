package the_fireplace.overlord.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.SetSquadsMessage;

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

    @SideOnly(Side.CLIENT)
    public static void makeClientInstance(EntityPlayer player, ArrayList<String> squadNames){
        instance = new Squads();
        if(player != null) {
            instance.setPlayerSquadNames(player.getUniqueID(), squadNames);
            Overlord.logDebug("Setting client squad names "+squadNames+" to player "+player.getName());
        }else if(Minecraft.getMinecraft().player != null) {
            instance.setPlayerSquadNames(Minecraft.getMinecraft().player.getUniqueID(), squadNames);
            Overlord.logInfo("Passed player was null, setting client player squad names to "+squadNames);
        }else{
            Overlord.logError("Unable to set client squads.");
        }
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

        if(FMLCommonHandler.instance().getMinecraftServerInstance() != null)
        if(FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer()){
            EntityPlayer playerMp = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getPlayerEntityByUUID(player);
            if(playerMp != null){
                if(!playerMp.world.isRemote && playerMp instanceof EntityPlayerMP)
                    PacketDispatcher.sendTo(new SetSquadsMessage(getSquadsFor(player)), (EntityPlayerMP)playerMp);
            }else{
                Overlord.logError("Could not find player with UUID "+player);
            }
        }
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
            Overlord.logError("Could not get save directory. Either you are connected to a server or Squads will not load properly.");
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
                Overlord.logError("Could not get save directory. Either you are connected to a server or Squads will not save properly.");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(saveDir, dataFileName)));
            out.writeObject(instance);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
