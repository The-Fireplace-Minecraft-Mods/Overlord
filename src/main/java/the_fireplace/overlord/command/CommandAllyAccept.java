package the_fireplace.overlord.command;

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

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class CommandAllyAccept extends CommandBase {
    @Nonnull
    @Override
    public String getName() {
        return "allyaccept";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            for(Alliance alliance:Overlord.instance.pendingAlliances){
                if(alliance.getUser2().getUUID().equals(((EntityPlayer) sender).getUniqueID().toString())){
                    Alliances.getInstance().addAlliance(alliance);
                    Overlord.instance.pendingAlliances.remove(alliance);
                    EntityPlayer ally = server.getEntityWorld().getPlayerEntityByUUID(UUID.fromString(alliance.getUser1().getUUID()));
                    if(ally != null) {
                        ally.sendMessage(new TextComponentTranslation("overlord.allyaccept", ((EntityPlayer) sender).getDisplayNameString()));
                        if(ally instanceof EntityPlayerMP)
                            if(((EntityPlayerMP) ally).getStatFile().canUnlockAchievement(Overlord.alliance))
                                ally.addStat(Overlord.alliance);
                    }
                    sender.sendMessage(new TextComponentTranslation("overlord.allyaccepted", alliance.getUser1().getPlayerName()));
                    if(sender instanceof EntityPlayerMP)
                        if(((EntityPlayerMP) sender).getStatFile().canUnlockAchievement(Overlord.alliance))
                            ((EntityPlayer)sender).addStat(Overlord.alliance);
                    return;
                }
            }
            sender.sendMessage(new TextComponentTranslation("overlord.nothingpending"));
        }
    }

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender icommandsender) {
        return "/allyaccept";
    }
}
