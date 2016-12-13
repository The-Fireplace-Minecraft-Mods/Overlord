package the_fireplace.overlord.command;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.tools.Alliance;
import the_fireplace.overlord.tools.Alliances;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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
                    if(ally != null) {
                        ally.addChatMessage(new TextComponentTranslation("overlord.allyaccept", ((EntityPlayer) sender).getDisplayNameString()));
                        if(ally instanceof EntityPlayerMP)
                            if(((EntityPlayerMP) ally).getStatFile().canUnlockAchievement(Overlord.alliance))
                                ally.addStat(Overlord.alliance);
                    }
                    sender.addChatMessage(new TextComponentTranslation("overlord.allyaccepted", alliance.getUser1().getPlayerName()));
                    if(sender instanceof EntityPlayerMP)
                        if(((EntityPlayerMP) sender).getStatFile().canUnlockAchievement(Overlord.alliance))
                            ((EntityPlayer)sender).addStat(Overlord.alliance);
                    return;
                }
            }
            sender.addChatMessage(new TextComponentTranslation("overlord.nothingpending"));
        }
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/allyaccept";
    }
}
