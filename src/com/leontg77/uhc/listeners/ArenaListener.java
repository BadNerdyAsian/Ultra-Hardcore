package com.leontg77.uhc.listeners;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.User;
import com.leontg77.uhc.User.Stat;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Arena listener class.
 * <p> 
 * Contains all eventhandlers for arena releated events.
 * 
 * @author LeonTG77
 */
public class ArenaListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Arena arena = Arena.getInstance();
		
		if (!arena.hasPlayer(player)) {
			return;
		}
		
		arena.removePlayer(player, false);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Arena arena = Arena.getInstance();
		
		Player player = event.getEntity();
		User user = User.get(player);
		
		if (!arena.hasPlayer(player)) {
			return;
		}
		
		arena.removePlayer(player, true);
		
    	event.setDeathMessage(null);
		event.setDroppedExp(0);

		user.increaseStat(Stat.ARENADEATHS);
		user.setStat(Stat.ARENACKS, 0);
		
		ItemStack head = new ItemStack(Material.GOLDEN_APPLE);
		ItemMeta headMeta = head.getItemMeta();
		headMeta.setDisplayName("§6Golden Head");
		headMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood."));
		head.setItemMeta(headMeta);
		
		event.getDrops().clear();
		event.getDrops().add(new ItemStack(Material.DIAMOND, 1));
		event.getDrops().add(new ItemStack(Material.ARROW, 32));
		event.getDrops().add(head);
		
		Player killer = player.getKiller();
		
		if (killer == null) {
			if (arena.killstreak.containsKey(player) && arena.killstreak.get(player) > 4) {
				PlayerUtils.broadcast(Arena.PREFIX + "§6" + player.getName() + "§7's killstreak of §a" + arena.killstreak.get(player) + " §7was shut down by PvE!");
			}

			player.sendMessage(Arena.PREFIX + "You got killed by PvE!");
			arena.killstreak.put(player, 0);
			
			arena.setScore("§8» §a§lPvE", arena.getScore("§8» §a§lPvE") + 1);
			arena.resetScore(player.getName());
			return;
		}
		
		if (arena.killstreak.containsKey(player) && arena.killstreak.get(player) > 4) {
			PlayerUtils.broadcast(Arena.PREFIX + "§6" + player.getName() + "§7's killstreak of §a" + arena.killstreak.get(player) + " §7was shut down by §6" + killer.getName() + "§7!");
		}
		
		player.sendMessage(Arena.PREFIX + "You got killed by §6" + killer.getName() + "§7! (" + NumberUtils.makePercent(killer.getHealth()) + "%§7)");
		killer.sendMessage(Arena.PREFIX + "You killed §a" + player.getName() + "§7!");
		arena.killstreak.put(player, 0);

		arena.setScore(killer.getName(), arena.getScore(killer.getName()) + 1);
		arena.resetScore(player.getName());
		
		killer.setLevel(killer.getLevel() + 1);
		User kUser = User.get(killer);
		
		kUser.increaseStat(Stat.ARENAKILLS);
		kUser.increaseStat(Stat.ARENACKS);
		
		if (kUser.getStat(Stat.ARENAKS) < kUser.getStat(Stat.ARENACKS)) {
			kUser.setStat(Stat.ARENAKS, kUser.getStat(Stat.ARENACKS));
		} 
		
		if (arena.killstreak.containsKey(killer)) {
			arena.killstreak.put(killer, arena.killstreak.get(killer) + 1);
		} else {
			arena.killstreak.put(killer, 1);
		}
		
		if (arena.killstreak.containsKey(killer)) {
			String killstreak = String.valueOf(arena.killstreak.get(killer));
			
			if (killstreak.endsWith("0") || killstreak.endsWith("5")) {
				PlayerUtils.broadcast(Arena.PREFIX + "§6" + killer.getName() + " §7is now on a §a" + killstreak + " §7killstreak!");
			}
		}
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();
		Inventory inv = event.getInventory();
		
		Arena arena = Arena.getInstance();
		
		if (arena.hasPlayer(player) && inv instanceof EnchantingInventory) {
			inv.setItem(1, new ItemStack (Material.INK_SACK, 3, (short) 4));
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Inventory inv = event.getInventory();
		Arena arena = Arena.getInstance();

		if (arena.hasPlayer(player) && inv instanceof EnchantingInventory) {
			inv.setItem(1, null);
		}
	}

	@EventHandler
	public void onEnchantItem(EnchantItemEvent event) {
		Player player = event.getEnchanter();
		Inventory inv = event.getInventory();
		
		Arena arena = Arena.getInstance();
		
		if (arena.hasPlayer(player)) {
			inv.setItem(1, new ItemStack (Material.INK_SACK, 3, (short) 4));
		}
	}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
        Arena arena = Arena.getInstance();
		
		if (arena.hasPlayer(player) && event.getClickedInventory() instanceof EnchantingInventory) {
			if (event.getSlot() == 1) {
				event.setCancelled(true);
			}
		}
	}
}