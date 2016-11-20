package the_fireplace.overlord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import the_fireplace.overlord.tools.Enemies;
import the_fireplace.overlord.tools.StringPair;

import java.util.ArrayList;

/**
 * @author The_Fireplace
 */
public class CommandEnemyList extends CommandBase {
    private static final Style blue = new Style().setColor(TextFormatting.BLUE);
    private static final Style purple = new Style().setColor(TextFormatting.LIGHT_PURPLE);
    private static final Style red = new Style().setColor(TextFormatting.RED);
    @Override
    public String getName() {
        return "enemylist";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            if(!Enemies.getInstance().getAllEnemies(((EntityPlayer) sender).getUniqueID()).isEmpty()) {
                sender.sendMessage(new TextComponentTranslation("overlord.enemylist"));
                ArrayList<StringPair> myenemies = Enemies.getInstance().getMyEnemies(((EntityPlayer) sender).getUniqueID());
                ArrayList<StringPair> enemiedme = Enemies.getInstance().getWhoEnemied(((EntityPlayer) sender).getUniqueID());
                for(StringPair enemy:Enemies.getInstance().getAllEnemies(((EntityPlayer) sender).getUniqueID())){
                    if(myenemies.contains(enemy) && !enemiedme.contains(enemy))
                        sender.sendMessage(new TextComponentString(enemy.getPlayerName()).setStyle(blue));
                    else if(myenemies.contains(enemy) && enemiedme.contains(enemy))
                        sender.sendMessage(new TextComponentString(enemy.getPlayerName()).setStyle(purple));
                    else
                        sender.sendMessage(new TextComponentString(enemy.getPlayerName()).setStyle(red));
                }
            }else{
                sender.sendMessage(new TextComponentTranslation("overlord.noenemies"));
            }
        }
    }

    @Override
    public String getUsage(ICommandSender icommandsender) {
        return "/enemylist";
    }
}
