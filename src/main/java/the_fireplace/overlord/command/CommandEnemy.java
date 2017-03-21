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
import the_fireplace.overlord.tools.Enemies;
import the_fireplace.overlord.tools.StringPair;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class CommandEnemy extends CommandBase {
    @Nonnull
    @Override
    public String getName() {
        return "enemy";
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
                    if(!Enemies.getInstance().getEnemies().contains(new Alliance(new StringPair(((EntityPlayer) sender).getUniqueID().toString(), ((EntityPlayer) sender).getDisplayNameString()), new StringPair(player.getUniqueID().toString(), player.getDisplayNameString())))) {
                        if(!Alliances.getInstance().isAlliedTo(((EntityPlayer) sender).getUniqueID(), player.getUniqueID())) {
                            Enemies.getInstance().addEnemies(new Alliance(new StringPair(((EntityPlayer) sender).getUniqueID().toString(), ((EntityPlayer) sender).getDisplayNameString()), new StringPair(player.getUniqueID().toString(), player.getDisplayNameString())));
                            player.sendMessage(new TextComponentTranslation("overlord.enemied", ((EntityPlayer) sender).getDisplayNameString()));
                            sender.sendMessage(new TextComponentTranslation("overlord.madeenemy", player.getDisplayNameString()));
                            for(Alliance alliance: Overlord.instance.pendingAlliances){
                                if(alliance.equals(new Alliance(new StringPair(((EntityPlayer) sender).getUniqueID().toString(), ((EntityPlayer) sender).getDisplayNameString()), new StringPair(player.getUniqueID().toString(), player.getDisplayNameString())))){
                                    Overlord.instance.pendingAlliances.remove(alliance);
                                }else if(alliance.equals(new Alliance(new StringPair(player.getUniqueID().toString(), player.getDisplayNameString()), new StringPair(((EntityPlayer) sender).getUniqueID().toString(), ((EntityPlayer) sender).getDisplayNameString())))){
                                    Overlord.instance.pendingAlliances.remove(alliance);
                                }
                            }
                            if(sender instanceof EntityPlayerMP)
                                if(((EntityPlayerMP) sender).getStatFile().canUnlockAchievement(Overlord.warmonger))
                                    ((EntityPlayer)sender).addStat(Overlord.warmonger);
                        }else{
                            player.sendMessage(new TextComponentTranslation("overlord.allytriedenemy", ((EntityPlayer) sender).getDisplayNameString()));
                            sender.sendMessage(new TextComponentTranslation("overlord.noenemyallied", player.getDisplayNameString()));
                        }
                    }else{
                        sender.sendMessage(new TextComponentTranslation("overlord.alreadyenemies", player.getDisplayNameString()));
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
        return "/enemy <PlayerName>";
    }
}
