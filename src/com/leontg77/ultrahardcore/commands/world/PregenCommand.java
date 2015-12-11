package com.leontg77.ultrahardcore.commands.world;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Pregen command class.
 * 
 * @author LeonTG77
 */
public class PregenCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.pregen")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 2) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("cancel")) {
					PlayerUtils.broadcast(Main.PREFIX + "Cancelling pregen.");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload fill cancel");
					return true;
				}
				
				if (args[0].equalsIgnoreCase("pause")) {
					PlayerUtils.broadcast(Main.PREFIX + "Pausing pregen.");
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload fill pause");
					return true;
				}
			}
			
			sender.sendMessage(Main.PREFIX + "Usage: /pregen <world> <radius>");
			return true;
		}
		
		World world = Bukkit.getServer().getWorld(args[0]);
		
		if (world == null) {
			sender.sendMessage(ChatColor.RED + args[0] + " is not an world.");
			return true;
		}
		
		int radius;
		
		try {
			radius = Integer.parseInt(args[1]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + args[1] + "is not a vaild radius");
			return true;
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "Starting pregen of world �a" + world.getName() + "�7.");
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " set " + radius + " 0 0");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload " + world.getName() + " fill 420 208 true");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pload fill confirm");
		return true;
	}
}