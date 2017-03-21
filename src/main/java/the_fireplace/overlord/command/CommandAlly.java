package the_fireplace.overlord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.tools.Alliance;
import the_fireplace.overlord.tools.Alliances;
import the_fireplace.overlord.tools.Enemies;
import the_fireplace.overlord.tools.StringPair;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class CommandAlly extends CommandBase {
    @Nonnull
    @Override
    public String getName() {
        return "ally";
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
                if(player != null){
                    if(Enemies.getInstance().isNotEnemiesWith(((EntityPlayer) sender).getUniqueID(), player.getUniqueID())) {
                        if (!Overlord.instance.pendingAlliances.contains(new Alliance(new StringPair(((EntityPlayer) sender).getUniqueID().toString(), ((EntityPlayer) sender).getDisplayNameString()), new StringPair(player.getUniqueID().toString(), player.getDisplayNameString())))) {
                            if (!Overlord.instance.pendingAlliances.contains(new Alliance(new StringPair(player.getUniqueID().toString(), player.getDisplayNameString()), new StringPair(((EntityPlayer) sender).getUniqueID().toString(), ((EntityPlayer) sender).getDisplayNameString())))) {
                                if (!Alliances.getInstance().isAlliedTo(((EntityPlayer) sender).getUniqueID(), player.getUniqueID())) {
                                    Overlord.instance.pendingAlliances.add(new Alliance(new StringPair(((EntityPlayer) sender).getUniqueID().toString(), ((EntityPlayer) sender).getDisplayNameString()), new StringPair(player.getUniqueID().toString(), player.getDisplayNameString())));
                                    player.sendMessage(new TextComponentTranslation("overlord.allyrequest", ((EntityPlayer) sender).getDisplayNameString()));
                                    sender.sendMessage(new TextComponentTranslation("overlord.requestsent", player.getDisplayNameString()));
                                } else {
                                    sender.sendMessage(new TextComponentTranslation("overlord.alreadyallied", player.getDisplayNameString()));
                                }
                            } else {
                                sender.sendMessage(new TextComponentTranslation("overlord.alreadyrequested.other", player.getDisplayNameString()));
                            }
                        } else {
                            sender.sendMessage(new TextComponentTranslation("overlord.alreadyrequested", player.getDisplayNameString()));
                        }
                    }else{
                        sender.sendMessage(new TextComponentTranslation("overlord.enemieswith", player.getDisplayNameString()));
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
        return "/ally <PlayerName>";
    }
}
