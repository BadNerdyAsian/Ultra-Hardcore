package com.leontg77.uhc.scenario.types;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.utils.BlockUtils;
import com.leontg77.uhc.utils.PlayerUtils;

public class CutClean extends Scenario implements Listener {
	private boolean enabled = false;
	
	public CutClean() {
		super("CutClean", "No furnaces required! Items requiring cooking drop their cooked variety.");
	}
	
	public void setEnabled(boolean enable) {
		enabled = enable;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		for (ItemStack drops : event.getDrops()) {
			if (drops.getType() == Material.PORK) {
				drops.setType(Material.GRILLED_PORK);
			}
			if (drops.getType() == Material.RAW_BEEF) {
				drops.setType(Material.COOKED_BEEF);
			}
			if (drops.getType() == Material.RAW_CHICKEN) {
				drops.setType(Material.COOKED_CHICKEN);
			}
			if (drops.getType() == Material.RABBIT) {
				drops.setType(Material.COOKED_RABBIT);
			}
			if (drops.getType() == Material.MUTTON) {
				drops.setType(Material.COOKED_MUTTON);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Block block = event.getBlock();
		
		if (block.getType() == Material.IRON_ORE) {
			if (ScenarioManager.getInstance().getScenario("TripleOres").isEnabled()) {
				return;
			}
			
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), 15);
			block.setType(Material.AIR);
			block.getState().update();
			ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(3);
			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.IRON_INGOT));
			item.setVelocity(new Vector(0, 0.2, 0));
		}
		
		if (block.getType() == Material.POTATO) {
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), 142);
			block.setType(Material.AIR);
			block.getState().update();
			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.BAKED_POTATO, 1 + new Random().nextInt(2)));
			item.setVelocity(new Vector(0, 0.2, 0));
		}
		
		if (block.getType() == Material.GOLD_ORE) {
			if (ScenarioManager.getInstance().getScenario("Barebones").isEnabled()) {
				return;
			}
			
			if (ScenarioManager.getInstance().getScenario("Goldless").isEnabled()) {
				return;
			}
			
			if (ScenarioManager.getInstance().getScenario("TripleOres").isEnabled()) {
				return;
			}
			
			if (!SpecInfo.locs.contains(event.getBlock().getLocation())) {
				Player player = event.getPlayer();
				int amount = 0;
				Location loc = event.getBlock().getLocation();
				
				for (int x = loc.getBlockX() - 1; x <= loc.getBlockX() + 1; x++) {
					for (int y = loc.getBlockY() - 1; y <= loc.getBlockY() + 1; y++) {
						for (int z = loc.getBlockZ() - 1; z <= loc.getBlockZ() + 1; z++) {
							if (loc.getWorld().getBlockAt(x, y, z).getType() == Material.GOLD_ORE) {
								amount++;
								SpecInfo.locs.add(loc.getWorld().getBlockAt(x, y, z).getLocation());
							}
						}
					}
				}
				
				if (SpecInfo.totalGold.containsKey(player.getName())) {
					SpecInfo.totalGold.put(player.getName(), SpecInfo.totalGold.get(player.getName()) + amount);
				} else {
					SpecInfo.totalGold.put(player.getName(), amount);
				}
				
				for (Player online : PlayerUtils.getPlayers()) {
					if (Spectator.getManager().isSpectating(online)) {
						online.sendMessage("[�9S�f] �7" + player.getName() + "�f:�6GOLD �f[V:�6" + amount + "�f] [T:�6" + SpecInfo.totalGold.get(player.getName()) + "�f]");
					}
				}
				amount = 0;
			}
			
			event.setCancelled(true);
			BlockUtils.blockCrack(event.getPlayer(), block.getLocation(), 14);
			block.setType(Material.AIR);
			block.getState().update();
			ExperienceOrb exp = (ExperienceOrb) event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.3, 0.5), ExperienceOrb.class);
			exp.setExperience(7);
			Item item = block.getWorld().dropItem(block.getLocation().add(0.5, 0.7, 0.5), new ItemStack (Material.GOLD_INGOT));
			item.setVelocity(new Vector(0, 0.2, 0));
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!isEnabled()) {
			return;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlockPlaced();
		
		if (block.getType() == Material.TNT) {
			block.setType(Material.AIR);
			Location loc = new Location(block.getWorld(), block.getLocation().getBlockX() + 0.5, block.getLocation().getBlockY() + 0.2, block.getLocation().getBlockZ() + 0.5);
        	TNTPrimed tnt = player.getWorld().spawn(loc, TNTPrimed.class);
        	tnt.setFuseTicks(80);
		}
	}
}