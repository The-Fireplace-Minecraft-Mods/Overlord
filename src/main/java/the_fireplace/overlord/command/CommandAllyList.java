package the_fireplace.overlord.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import the_fireplace.overlord.tools.Alliances;
import the_fireplace.overlord.tools.StringPair;

import javax.annotation.Nonnull;

/**
 * @author The_Fireplace
 */
public class CommandAllyList extends CommandBase {
	@Nonnull
	@Override
	public String getName() {
		return "allylist";
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
			if (!Alliances.getInstance().getAllies(((EntityPlayer) sender).getUniqueID()).isEmpty()) {
				sender.sendMessage(new TextComponentTranslation("overlord.allylist"));
				for (StringPair pait : Alliances.getInstance().getAllies(((EntityPlayer) sender).getUniqueID())) {
					sender.sendMessage(new TextComponentString(pait.getPlayerName()));
				}
			} else {
				sender.sendMessage(new TextComponentTranslation("overlord.nofriends"));
			}
		}
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender icommandsender) {
		return "/allylist";
	}
}
