package the_fireplace.overlord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.PendingAllianceMessage;
import the_fireplace.overlord.tools.Alliance;
import the_fireplace.overlord.tools.StringPair;

/**
 * @author The_Fireplace
 */
public class CommandAlly extends CommandBase {
    @Override
    public String getCommandName() {
        return "ally";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            if(args.length == 1){
                for(EntityPlayerMP player:server.getPlayerList().getPlayerList()){
                    if(player.getDisplayNameString().equals(args[0])){
                        if(!Overlord.instance.pendingAlliances.contains(new Alliance(new StringPair(((EntityPlayer) sender).getUniqueID().toString(), ((EntityPlayer) sender).getDisplayNameString()), new StringPair(player.getUniqueID().toString(), player.getDisplayNameString())))) {
                            Overlord.instance.pendingAlliances.add(new Alliance(new StringPair(((EntityPlayer) sender).getUniqueID().toString(), ((EntityPlayer) sender).getDisplayNameString()), new StringPair(player.getUniqueID().toString(), player.getDisplayNameString())));
                            PacketDispatcher.sendTo(new PendingAllianceMessage(((EntityPlayer) sender).getDisplayNameString()), player);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/ally <PlayerName>";
    }
}
