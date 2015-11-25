package com.leontg77.uhc.cmds;

import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.User;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Mute command class
 * 
 * @author LeonTG77
 */
public class MuteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.mute")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /mute <player> [time] [reason]");
			return true;
		}

		Player target = Bukkit.getServer().getPlayer(args[0]);
		
		if (target == null) {
			sender.sendMessage(ChatColor.RED + "That player is not online.");
			return true;
		}
    	
    	if (target.hasPermission("uhc.staff") && !sender.hasPermission("uhc.mute.bypass")) {
    		sender.sendMessage(Main.PREFIX + "You cannot mute this player.");
    		return true;
    	}
		
		User user = User.get(target);

		if (user.isMuted()) {
			user.unmute();
			
			PlayerUtils.broadcast(Main.PREFIX + "�6" + target.getName() + " �7has been unmuted.");
			target.sendMessage(Main.PREFIX + "You are no longer muted.");
			return true;
		} 
		
		if (args.length < 3) {
			sender.sendMessage(Main.PREFIX + "Usage: /mute <player> <time> <reason>");
			return true;
		}
		
		StringBuilder message = new StringBuilder("");
		
    	for (int i = 2; i < args.length; i++) {
    		message.append(args[i]).append(" ");
    	}
    	
    	String reason = message.toString().trim();
    	
		long time = DateUtils.parseDateDiff(args[1], true);
		
		PlayerUtils.broadcast(Main.PREFIX + "�6" + target.getName() + " �7has been " + (time == 0 ? "muted" : "temp-muted") + " for �a" + reason + (time == 0 ? "�7." : "�7. �8(�a" + DateUtils.formatDateDiff(time) + "�8)"));
		target.sendMessage(Main.PREFIX + "You have been muted for �a" + reason + "�7.");
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		
		user.mute(reason, (time <= 0 ? null : new Date(time)));
		return true;
	}
}