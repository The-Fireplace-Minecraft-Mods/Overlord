package the_fireplace.overlord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.tools.Alliance;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class CommandAllyReject extends CommandBase {
    @Nonnull
    @Override
    public String getName() {
        return "allyreject";
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

    @Nonnull
    @Override
    public String getUsage(@Nonnull ICommandSender icommandsender) {
        return "/allyreject";
    }
}
