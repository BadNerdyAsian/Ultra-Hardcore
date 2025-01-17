package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Vote command class
 * 
 * @author LeonTG77
 */
public class VoteCommand implements CommandExecutor {
	public static ArrayList<String> voted = new ArrayList<String>();
	
	public static boolean running = false;
	public static int yes = 0;
	public static int no = 0;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.vote")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /vote <message|end>");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("end")) {
			if (!running) {
				sender.sendMessage(ChatColor.RED + "No votes are running.");
				return true;
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "The vote has ended, the results are: �a" + yes + " yes �7and �c" + no + " no�7.");
			
			running = false;
			voted.clear();
			yes = 0;
			no = 0;
			return true;
		}
		
		if (running) {
			sender.sendMessage(ChatColor.RED + "Theres already a vote running.");
			return true;
		}
		
		StringBuilder message = new StringBuilder();
           
        for (int i = 0; i < args.length; i++) {
        	message.append(args[i]).append(" ");
        }
        
        String msg = message.toString().trim();
        
        running = true;
        voted.clear();
		yes = 0;
		no = 0;
        
        PlayerUtils.broadcast(Main.PREFIX + "A vote has started for: �6" + msg + "�7.");
        PlayerUtils.broadcast("�8� �7Say �a'y'�7 or �c'n'�7 in chat to vote.");
		return true;
	}
}