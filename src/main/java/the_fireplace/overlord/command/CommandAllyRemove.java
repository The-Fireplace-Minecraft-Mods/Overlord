package the_fireplace.overlord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.TerminatedAllianceMessage;
import the_fireplace.overlord.tools.Alliance;
import the_fireplace.overlord.tools.Alliances;

/**
 * @author The_Fireplace
 */
public class CommandAllyRemove extends CommandBase {
    @Override
    public String getCommandName() {
        return "allyremove";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            if(args.length == 1){
                EntityPlayer player = server.getEntityWorld().getPlayerEntityByName(args[0]);
                if(player != null)
                if(Alliances.getInstance().isAlliedTo(((EntityPlayer) sender).getUniqueID(), player.getUniqueID())) {
                    for(Alliance alliance:Alliances.getInstance().getAlliances()){
                        if(alliance.getUser1().getUUID().equals(((EntityPlayer) sender).getUniqueID().toString()) && alliance.getUser2().getUUID().equals(player.getUniqueID().toString())){
                            Alliances.getInstance().removeAlliance(alliance);
                        }else if(alliance.getUser2().getUUID().equals(((EntityPlayer) sender).getUniqueID().toString()) && alliance.getUser1().getUUID().equals(player.getUniqueID().toString())){
                            Alliances.getInstance().removeAlliance(alliance);
                        }
                    }
                    PacketDispatcher.sendTo(new TerminatedAllianceMessage(((EntityPlayer) sender).getDisplayNameString()), (EntityPlayerMP)player);
                }
            }
        }
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/allyremove <PlayerName>";
    }
}
