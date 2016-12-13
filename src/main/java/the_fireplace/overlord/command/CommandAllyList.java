package the_fireplace.overlord.command;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import the_fireplace.overlord.tools.Alliances;
import the_fireplace.overlord.tools.StringPair;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author The_Fireplace
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommandAllyList extends CommandBase {
    @Override
    public String getCommandName() {
        return "allylist";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            if(!Alliances.getInstance().getAllies(((EntityPlayer) sender).getUniqueID()).isEmpty()) {
                sender.addChatMessage(new TextComponentTranslation("overlord.allylist"));
                for(StringPair pair:Alliances.getInstance().getAllies(((EntityPlayer) sender).getUniqueID())){
                    sender.addChatMessage(new TextComponentString(pair.getPlayerName()));
                }
            }else{
                sender.addChatMessage(new TextComponentTranslation("overlord.nofriends"));
            }
        }
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/allylist";
    }
}
