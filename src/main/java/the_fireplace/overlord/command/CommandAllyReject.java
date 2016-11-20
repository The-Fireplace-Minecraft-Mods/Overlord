package the_fireplace.overlord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.tools.Alliance;

import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class CommandAllyReject extends CommandBase {
    @Override
    public String getName() {
        return "allyreject";
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
                    Overlord.instance.pendingAlliances.remove(alliance);
                    EntityPlayer nonally = server.getEntityWorld().getPlayerEntityByUUID(UUID.fromString(alliance.getUser1().getUUID()));
                    if(nonally != null)
                        nonally.sendMessage(new TextComponentTranslation("overlord.allyreject", ((EntityPlayer) sender).getDisplayNameString()));
                    sender.sendMessage(new TextComponentTranslation("overlord.allyrejected", alliance.getUser1().getPlayerName()));
                    return;
                }
            }
            sender.sendMessage(new TextComponentTranslation("overlord.nothingpending"));
        }
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "/allyreject";
    }
}
