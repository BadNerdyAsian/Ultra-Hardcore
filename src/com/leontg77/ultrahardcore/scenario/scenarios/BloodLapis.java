package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * BloodLapis scenario class
 * 
 * @author LeonTG77
 */
public class BloodLapis extends Scenario implements Listener {
	
	public BloodLapis() {
		super("BloodLapis", "Every time you mine lapis you take half a heart.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}

	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
    	
    	if (block.getType() != Material.LAPIS_ORE) {
    		return;
    	}
    	
		player.damage(1);
    }
}