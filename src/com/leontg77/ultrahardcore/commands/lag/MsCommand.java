package com.leontg77.ultrahardcore.commands.lag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Ms command class.
 * 
 * @author LeonTG77
 */
public class MsCommand extends UHCCommand {
	
	public MsCommand() {
		super("ms", "[player]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		Player target;
		int ping;
		
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can view their own ping.");
			}
			
			target = (Player) sender;
		} else {
			target = Bukkit.getPlayer(args[0]);
		}
		
		if (target == null) {
			throw new CommandException("'" + args[0] + "' is not online.");
		}
		
		ping = PlayerUtils.getPing(target);
		
		// Not calculated yet
		if (ping == 0) { 
			throw new CommandException((sender == target ? "Your" : target.getName() + "'s") + " ping hasn't been calculated yet, Try again later!");
		}
		
		ChatColor color;
		String status;
		
		if (ping < 0) { 
			color = ChatColor.RED;
			status = "Wat..?";
		} else if (ping < 75) { 
			color = ChatColor.GREEN;
			status = "Good";
		} else if (ping < 150) { 
			color = ChatColor.DARK_GREEN;
			status = "Decent";
		} else if (ping < 250) { 
			color = ChatColor.GOLD;
			status = "Mediocre";
		} else if (ping < 400) { 
			color = ChatColor.RED;
			status = "Bad";
		} else { 
			color = ChatColor.DARK_RED;
			status = "Horrible";
		}
		
		sender.sendMessage(Main.PREFIX + (sender == target ? "Your" : target.getName() + "'s") + " ping: " + color + ping + "ms �8(�6" + status + "�8)");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return null;
		}
		
		return new ArrayList<String>();
	}
}