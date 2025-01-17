package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Timers;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BestBTC scenario class
 * 
 * @author LeonTG77
 */
public class BestBTC extends Scenario implements Listener, CommandExecutor {
	private static HashSet<String> list = new HashSet<String>();
	private BukkitRunnable task = null;

	public BestBTC() {
		super("BestBTC", "After PvP enables, for every 10 minutes you are under Y=50, you gain a heart. Going above Y=50 will take you off the list. To get back on, you must mine a diamond.");
		
		Bukkit.getPluginCommand("btc").setExecutor(this);
		Bukkit.getPluginCommand("btclist").setExecutor(this);
	}
	
	@Override
	public void onDisable() {
		list.clear();
		task.cancel();
		task = null;
	}
	
	@Override
	public void onEnable() {
		list.clear();
	}
	
	@EventHandler
	public void on(PvPEnableEvent event) {
		for (Player online : PlayerUtils.getPlayers()) {
			list.add(online.getName());
		}
		
		task = new BukkitRunnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					if (!list.contains(online.getName())) {
						online.sendMessage(ChatColor.GREEN + "BestBTC players gained a heart!");
						continue;
					}

					online.sendMessage(ChatColor.GREEN + "You were rewarded for your BTC skills!");
					
					online.setMaxHealth(online.getMaxHealth() + 2);
					online.setHealth(online.getHealth() + 2);
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 12000, 12000);
	}
	
	/**
	 * Get the Best BTC list.
	 * 
	 * @return The list.
	 */
	public static Set<String> getList() {
		return list;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		if (list.contains(player.getName())) {
			return;
		}

		if (block.getType() != Material.DIAMOND_ORE) {
			return;
		}
		
		PlayerUtils.broadcast(ChatColor.GREEN + player.getName() + " mined a diamond! He is back on the Best BTC List!");
		list.add(player.getName());
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if (event.getTo().getBlockY() <= 50) {
			return;
		}

		if (!list.contains(player.getName())) {
			return;
		}
		
		if (Timers.time < 20) {
			return;
		}
		
		PlayerUtils.broadcast(ChatColor.RED + player.getName() + " moved above y:50!");
		list.remove(player.getName());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
            sender.sendMessage(ChatColor.RED + "Best BTC game is not in progress!");
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("btclist")) {
			sender.sendMessage(ChatColor.GREEN + "Best BTC players:");
			
            for (String player : list) {
                sender.sendMessage(ChatColor.YELLOW + player);
            }
		}
		
		if (cmd.getName().equalsIgnoreCase("btc")) {
			if (!sender.hasPermission("uhc.bestbtc")) {
				sender.sendMessage(Main.NO_PERM_MSG);
				return true;
			}
			
			if (args.length < 2) {
				sender.sendMessage(ChatColor.GREEN + "Help for BestBTC:");
				sender.sendMessage("�7- �f/btc add <player> - �oAdd a player to the list.");
				sender.sendMessage("�7- �f/btc remove <player> - �oRemove a player from the list.");
				return true;
			}
			
			String player = args[1];
			
			if (args[0].equalsIgnoreCase("add")) {
				if (list.contains(player)) {
	                sender.sendMessage(ChatColor.RED + player + " is already on the list!");
					return true;
				}

                sender.sendMessage(ChatColor.GOLD + player + " was added to the list!");
				list.add(player);
				return true;
			} 
				
			if (args[0].equalsIgnoreCase("remove")) {
				if (!list.contains(player)) {
	                sender.sendMessage(ChatColor.RED + player + " is not present on the list!");
					return true;
				}
				
                sender.sendMessage(ChatColor.GOLD + player + " was removed from the list!");
				list.remove(player);
				return true;
			}

			sender.sendMessage(ChatColor.GREEN + "Help for BestBTC:");
			sender.sendMessage("�7- �f/btc add <player> - �oAdd a player to the list.");
			sender.sendMessage("�7- �f/btc remove <player> - �oRemove a player from the list.");
		}
		return true;
	}
}