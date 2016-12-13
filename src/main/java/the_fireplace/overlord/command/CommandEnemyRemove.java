package the_fireplace.overlord.command;

import mcp.MethodsReturnNonnullByDefault;
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
import the_fireplace.overlord.tools.Enemies;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author The_Fireplace
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommandEnemyRemove extends CommandBase {
    @Override
    public String getCommandName() {
        return "enemyremove";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            if(args.length == 1){
                EntityPlayer player = server.getEntityWorld().getPlayerEntityByName(args[0]);
                if(player != null) {
                    if (Enemies.getInstance().considersPlayerEnemy(((EntityPlayer) sender).getUniqueID(), player.getUniqueID())) {
                        for (Alliance playerPair : Enemies.getInstance().getEnemies()) {
                            if (playerPair.getUser1().getUUID().equals(((EntityPlayer) sender).getUniqueID().toString()) && playerPair.getUser2().getUUID().equals(player.getUniqueID().toString())) {
                                Enemies.getInstance().removeEnemies(playerPair);
                                break;
                            }
                        }
                        player.addChatMessage(new TextComponentTranslation("overlord.enemytermination", ((EntityPlayer) sender).getDisplayNameString()));
                        sender.addChatMessage(new TextComponentTranslation("overlord.enemyterminated", player.getDisplayNameString()));
                        if(sender instanceof EntityPlayerMP)
                            if(((EntityPlayerMP) sender).getStatFile().canUnlockAchievement(Overlord.forgiver))
                                ((EntityPlayer)sender).addStat(Overlord.forgiver);
                    } else {
                        sender.addChatMessage(new TextComponentTranslation("overlord.notenemied", player.getDisplayNameString()));
                    }
                }else{
                    sender.addChatMessage(new TextComponentTranslation("commands.generic.player.notFound"));
                }
            }else{
                throw new WrongUsageException(getCommandUsage(sender));
            }
        }
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "/enemyremove <PlayerName>";
    }
}
