package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * RewardingLongshots scenario class
 * 
 * @author LeonTG77
 */
public class RewardingLongshots extends Scenario implements Listener {
	
	public RewardingLongshots() {
		super("RewardingLongshots", "When shooting and hitting people with a bow from a variable distance, you will be rewarded with various different items, the + means it gives you bows/arrows for getting longshots as well.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Arrow) || !(event.getEntity() instanceof Player)) {
			return;
		}
	
		Player player = (Player) event.getEntity();
		Arrow damager = (Arrow) event.getDamager();
		
		if (!(damager.getShooter() instanceof Player)) {
			return;
		}
		
		Player killer = (Player) damager.getShooter();
		double distance = killer.getLocation().distance(player.getLocation());
		
		if (distance < 30) {
			return;
		}

		if (distance <= 49) {
			PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
			return;
		}

		PlayerUtils.broadcast("§9RewardingLongshots §8» §6" + killer.getName() + " §7got a longshot of §6" + NumberUtils.convertDouble(distance) + " §7blocks!");

		if (distance <= 99) {
			PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT));
			PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
			return;
		} 

		if (distance <= 199) {
			PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT));
			PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT));
			PlayerUtils.giveItem(killer, new ItemStack(Material.DIAMOND));
			return;
		}

		PlayerUtils.giveItem(killer, new ItemStack(Material.GOLD_INGOT, 3));
		PlayerUtils.giveItem(killer, new ItemStack(Material.IRON_INGOT, 2));
		PlayerUtils.giveItem(killer, new ItemStack(Material.DIAMOND, 5));
	}
}
