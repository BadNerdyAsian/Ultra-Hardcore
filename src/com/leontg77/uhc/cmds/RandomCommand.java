package com.leontg77.uhc.cmds;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.Teams;
import com.leontg77.uhc.util.PlayerUtils;

public class RandomCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (cmd.getName().equalsIgnoreCase("random")) {
			if (sender.hasPermission("uhc.random")) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "Usage: /random <size> [playersnotplaying...]");
					return true;
				}
				
				if (args.length == 1) {
					int size = 1;
					
					try {
						size = Integer.parseInt(args[0]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Invaild team.");
						return true;
					}
					
					ArrayList<Player> a = new ArrayList<Player>();
					
					for (Player online : PlayerUtils.getPlayers()) {
						if (online.getScoreboard().getEntryTeam(online.getName()) == null) {
							a.add(online);
						}
					}

					Team t = null;
					
					for (Team team : Teams.getManager().getTeams()) {
						if (team.getSize() == 0) {
							t = team;
							break;
						}
					}
					
					if (t == null) {
						sender.sendMessage(ChatColor.RED + "No more available teams.");
						return true;
					}
					
					for (int i = 0; i < size; i++) {
						Player p = a.get(i);
						t.addEntry(p.getName());
						p.sendMessage(Main.prefix() + "You were added to team " + t.getName());
					}

					sender.sendMessage(Main.prefix() + "Created a rTo" + size + " using team " + t.getName() + ".");
					return true;
				}
				
				int size = 1;
				
				try {
					size = Integer.parseInt(args[0]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Invaild team.");
					return true;
				}
				
				ArrayList<Player> a = new ArrayList<Player>();
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (online.getScoreboard().getEntryTeam(online.getName()) == null) {
						a.add(online);
					}
				}
				
				for (int i = 1; i < args.length; i++) {
					if (a.contains(args[i])) {
						a.remove(args[i]);
					}
				}

				Team t = null;
				
				for (Team team : Teams.getManager().getTeams()) {
					if (team.getSize() == 0) {
						t = team;
						break;
					}
				}
				
				if (t == null) {
					sender.sendMessage(ChatColor.RED + "No more available teams.");
					return true;
				}
				
				for (int i = 0; i < size; i++) {
					if (a.size() < i) {
						sender.sendMessage(ChatColor.RED + "Could not add a player to team " + t.getName() + ".");
						continue;
					}
					Player p = a.get(i);
					t.addEntry(p.getName());
					p.sendMessage(Main.prefix() + "You were added to team " + t.getName());
				}
				
				sender.sendMessage(Main.prefix() + "Created a rTo" + size + " using team " + t.getName() + ".");
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
		}
		return true;
	}
}