package the_fireplace.overlord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.network.AllyAcceptMessage;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.tools.Alliance;
import the_fireplace.overlord.tools.Alliances;

import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class CommandAllyAccept extends CommandBase {
    @Override
    public String getCommandName() {
        return "allyaccept";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            for(Alliance alliance:Overlord.instance.pendingAlliances){
                if(alliance.getUser2().getUUID().equals(((EntityPlayer) sender).getUniqueID().toString())){
                    Alliances.getInstance().addAlliance(alliance);
                    Overlord.instance.pendingAlliances.remove(alliance);
                    EntityPlayer ally = server.getEntityWorld().getPlayerEntityByUUID(UUID.fromString(alliance.getUser1().getUUID()));
                    if(ally != null)
                        PacketDispatcher.sendTo(new AllyAcceptMessage(((EntityPlayer) sender).getDisplayNameString()), (EntityPlayerMP)ally);
                    return;
                }
            }
        }
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/allyaccept";
    }
}
