package the_fireplace.overlord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.tools.Alliance;
import the_fireplace.overlord.tools.Alliances;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class CommandAllyRemove extends CommandBase {
    @Nonnull
    @Override
    public String getName() {
        return "allyremove";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            if(args.length == 1){
                EntityPlayer player = server.getEntityWorld().getPlayerEntityByName(args[0]);
                if(player != null) {
                    if (Alliances.getInstance().isAlliedTo(((EntityPlayer) sender).getUniqueID(), player.getUniqueID())) {
                        for (Alliance alliance : Alliances.getInstance().getAlliances()) {
                            if (alliance.getUser1().getUUID().equals(((EntityPlayer) sender).getUniqueID().toString()) && alliance.getUser2().getUUID().equals(player.getUniqueID().toString())) {
                                Alliances.getInstance().removeAlliance(alliance);
                                break;
                            } else if (alliance.getUser2().getUUID().equals(((EntityPlayer) sender).getUniqueID().toString()) && alliance.getUser1().getUUID().equals(player.getUniqueID().toString())) {
                                Alliances.getInstance().removeAlliance(alliance);
                                break;
                            }
                        }
                        if(sender instanceof EntityPlayerMP)
                            if(((EntityPlayerMP) sender).getStatFile().canUnlockAchievement(Overlord.breakalliance))
                                ((EntityPlayer)sender).addStat(Overlord.breakalliance);
                        player.sendMessage(new TextComponentTranslation("overlord.allytermination", ((EntityPlayer) sender).getDisplayNameString()));
                        sender.sendMessage(new TextComponentTranslation("overlord.allyterminated", player.getDisplayNameString()));
                    } else {
                        sender.sendMessage(new TextComponentTranslation("overlord.notallied", player.getDisplayNameString()));
                    }
                }else{
                    sender.sendMessage(new TextComponentTranslation("commands.generic.player.notFound"));
                }
            }else{
                throw new WrongUsageException(getUsage(sender));
            }
        }
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender icommandsender) {
        return "/allyremove <PlayerName>";
    }
}
