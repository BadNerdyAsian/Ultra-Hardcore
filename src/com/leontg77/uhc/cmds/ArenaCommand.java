package com.leontg77.uhc.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.scoreboard.Scoreboards;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Arena command class.
 * 
 * @author LeonTG77
 */
public class ArenaCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Arena arena = Arena.getInstance();
		Game game = Game.getInstance();
		
		if (sender.hasPermission("uhc.arena")) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("enable")) {
					if (arena.isEnabled()) {
						sender.sendMessage(Arena.PREFIX + "Arena is already enabled.");
						return true;
					}
					
					PlayerUtils.broadcast(Arena.PREFIX + "The arena has been enabled.");
					arena.enable();
					return true;
				} 

				if (args[0].equalsIgnoreCase("disable")) {
					if (!arena.isEnabled()) {
						sender.sendMessage(Arena.PREFIX + "Arena is not enabled.");
						return true;
					}

					PlayerUtils.broadcast(Arena.PREFIX + "The arena has been disabled.");
					arena.disable();
					return true;
				} 

				if (args[0].equalsIgnoreCase("reset")) {
					arena.reset();
					return true;
				} 

				if (args[0].equalsIgnoreCase("board")) {
					if (game.arenaBoard()) {
						for (String entry : arena.board.getEntries()) {
							arena.resetScore(entry);
						}
						
						PlayerUtils.broadcast(Arena.PREFIX + "The arena board has been disabled.");
						game.setArenaBoard(false);
						
						Scoreboards board = Scoreboards.getInstance();
						board.kills.setDisplaySlot(DisplaySlot.SIDEBAR);
					} else {
						PlayerUtils.broadcast(Arena.PREFIX + "The arena board has been enabled.");
						arena.arenaKills.setDisplaySlot(DisplaySlot.SIDEBAR);
						
						game.setPregameBoard(false);
						game.setArenaBoard(true);

						arena.setScore("�8� �a�lPvE", 1);
						arena.setScore("�8� �a�lPvE", 0);
					}
					return true;
				}
			}
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use the arena.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args.length > 0 && args[0].equalsIgnoreCase("leave")) {
			if (arena.isEnabled()) {
				if (!arena.hasPlayer(player)) {
					player.sendMessage(Arena.PREFIX + "You are not in the arena.");
					return true;
				}
				
				arena.removePlayer(player, false);;
			} else {
				player.sendMessage(Arena.PREFIX + "The arena is currently disabled.");
			}
			return true;
		}
		
		if (arena.isEnabled()) {
			if (arena.hasPlayer(player)) {
				player.sendMessage(Arena.PREFIX + "You are already in the arena.");
				return true;
			}
			
			arena.addPlayer(player);
		} else {
			player.sendMessage(Arena.PREFIX + "The arena is currently disabled.");
		}
		return true;
	}
}