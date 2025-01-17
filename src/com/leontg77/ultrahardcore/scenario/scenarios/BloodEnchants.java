package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * BloodEnchants scenario class
 * 
 * @author LeonTG77
 */
public class BloodEnchants extends Scenario implements Listener {
	
	public BloodEnchants() {
		super("BloodEnchants", "When you enchant any item for any amount of levels, you take half a heart of damage.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}


	@EventHandler
	public void onEnchantItem(EnchantItemEvent event) {
		Player player = event.getEnchanter();
		player.damage(1);
	}
}