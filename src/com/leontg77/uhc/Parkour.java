package com.leontg77.uhc;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * The parkour class.
 * 
 * @author LeonTG77
 */
public class Parkour implements Listener {
	private static Parkour manager = new Parkour();
	public BukkitRunnable task;

	private Location spawn = new Location(Bukkit.getWorld("lobby"), -34.5, 32, 0.5, 90, 0);
	private Location point1 = new Location(Bukkit.getWorld("lobby"), -80.5, 38.5, -1.5, 90, 0);
	private Location point2 = new Location(Bukkit.getWorld("lobby"), -135.5, 36.5, -12.5, 90, 0);
	private Location point3 = new Location(Bukkit.getWorld("lobby"), -159.5, 29.5, 8.5, 0, 0);
	
	private HashSet<Player> players = new HashSet<Player>();
	private HashMap<Player, Integer> checkpoint = new HashMap<Player, Integer>();
	private HashMap<Player, Integer> time = new HashMap<Player, Integer>();

	/**
	 * Gets the instance of this class
	 * 
	 * @return The instance.
	 */
	public static Parkour getInstance() {
		return manager;
	}
	
	/**
	 * Set up the parkour system.
	 */
	public void setup() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
		
		task = new BukkitRunnable() {
			public void run() {
				for (Player parkourers : time.keySet()) {
					time.put(parkourers, time.get(parkourers) + 1);
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 20, 20);
	}
	
	/**
	 * Reset the parkour data.
	 */
	public void reset() {
		checkpoint.clear();
		players.clear();
		time.clear();
	}
	
	public Location getLocation(int checkpoint) {
		if (checkpoint == 1) {
			return point1;
		}
		else if (checkpoint == 2) {
			return point2;
		}
		else if (checkpoint == 3) {
			return point3;
		}
		else {
			return spawn;
		}
	}
	
	public boolean isParkouring(Player player) {
		return players.contains(player);
	}
	
	public Integer getCheckpoint(Player player) {
		return checkpoint.get(player);
	}
	
	public Integer getTime(Player player) {
		return time.get(player);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (players.contains(player)) {
			players.remove(player);
		}
		
		if (checkpoint.containsKey(player)) {
			checkpoint.remove(player);
		}
		
		if (time.containsKey(player)) {
			time.remove(player);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
			return;
		}
		
		Player player = event.getPlayer();
		Entity point;
		
		try {
			point = PlayerUtils.getNearby(event.getTo(), 0.5).get(0);
		} catch (Exception e) {
			return;
		}
		
		if (point instanceof ArmorStand) {
			if (((ArmorStand) point).getCustomName() == null) {
				return;
			}
			
			if (((ArmorStand) point).getCustomName().contains("Start")) {
				if (players.contains(player)) {
					player.sendMessage(Main.PREFIX + "The timer has been reset to �a0s�7.");
					player.playSound(player.getLocation(), "random.pop", 1, 1);
					time.put(player, 0);
					return;
				}
				
				player.sendMessage(Main.PREFIX + "Parkour started.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				players.add(player);
				checkpoint.put(player, 0);
				time.put(player, 0);
			}
			
			if (!players.contains(player)) {
				return;
			}
			
			if (((ArmorStand) point).getCustomName().contains("#1")) {
				if (checkpoint.containsKey(player) && checkpoint.get(player) == 1) {
					return;
				}
				
				player.sendMessage(Main.PREFIX + "You reached checkpoint �c1�7.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				players.add(player);
				checkpoint.put(player, 1);
			}
			
			if (((ArmorStand) point).getCustomName().contains("#2")) {
				if (checkpoint.containsKey(player) && checkpoint.get(player) == 2) {
					return;
				}
				
				player.sendMessage(Main.PREFIX + "You reached checkpoint �c2�7.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				players.add(player);
				checkpoint.put(player, 2);
			}
			
			if (((ArmorStand) point).getCustomName().contains("#3")) {
				if (checkpoint.containsKey(player) && checkpoint.get(player) == 3) {
					return;
				}
				
				player.sendMessage(Main.PREFIX + "You reached checkpoint �c3�7.");
				player.playSound(player.getLocation(), "random.pop", 1, 1);
				players.add(player);
				checkpoint.put(player, 3);
			}
			
			if (((ArmorStand) point).getCustomName().contains("finish")) {
				player.sendMessage(Main.PREFIX + "You finished the parkour, time used: �a" + DateUtils.ticksToString(time.get(player)));
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
				
				players.remove(player);
				checkpoint.remove(player);
				time.remove(player);
			}
		}
	}
}