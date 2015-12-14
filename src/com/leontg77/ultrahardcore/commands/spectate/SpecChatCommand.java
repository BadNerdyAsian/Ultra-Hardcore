package com.leontg77.ultrahardcore.commands.spectate;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Spectator;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * SpecChat command class
 * 
 * @author LeonTG77
 */
public class SpecChatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can talk in spec-chat.");
			return true;
		}
		
        Spectator spec = Spectator.getInstance();
        Player player = (Player) sender;
        
		if (!spec.isSpectating(player)) {
			sender.sendMessage(Main.PREFIX + "You are not a spectator.");
			return true;
		}
		
		if (args.length == 0) {
    		sender.sendMessage(Main.PREFIX + "Usage: /sc <message>");
        	return true;
        } 
        
    	StringBuilder message = new StringBuilder("");
		
		for (int i = 0; i < args.length; i++) {
			message.append(args[i]).append(" ");
		}

        String msg = "�8[�5SpecChat�8] �7" + sender.getName() + "�8 � �f" + message.toString().trim();
        
        for (Player online : PlayerUtils.getPlayers()) {
        	if (!spec.isSpectating(online)) {
        		continue;
        	}
        	
        	online.sendMessage(msg);
        }
		return true;
	}
}