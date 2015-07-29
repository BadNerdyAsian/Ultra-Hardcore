package com.leontg77.uhc.scenario.types;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.util.BlockUtils;

@SuppressWarnings("deprecation")
public class FlowerPower extends Scenario implements Listener {
	private boolean enabled = false;
	
	public FlowerPower() {
		super("FlowerPower", "If you break flowers they will drop an random item.");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		if (event.getBlock().getType() == Material.RED_ROSE || event.getBlock().getType() == Material.YELLOW_FLOWER || event.getBlock().getType() == Material.DOUBLE_PLANT) {
			Block block = event.getBlock();
			
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), block.getTypeId());
			block.setType(Material.AIR);
			block.getState().update();
			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), randomItem());
			item.setVelocity(new Vector(0, 0.2, 0));
		}
	}

	private ItemStack randomItem() {
		Random r = new Random();
		Material m = Material.values()[r.nextInt(Material.values().length)];
		int a = 1 + r.nextInt(2);
		return new ItemStack (m, a);
	}
}