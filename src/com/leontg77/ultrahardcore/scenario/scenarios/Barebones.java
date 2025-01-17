package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Barebones scenario class
 * 
 * @author LeonTG77
 */
public class Barebones extends Scenario implements Listener {
	
	public Barebones() {
		super("Barebones", "The Nether is disabled, and iron is the highest tier you can obtain through gearing up. When a player dies, they will drop 1 diamond, 1 golden apple, 32 arrows, and 2 string. You cannot craft an enchantment table, anvil, or golden apple. Mining any ore except coal or iron will drop an iron ingot.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}

	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		boolean cutclean = ScenarioManager.getInstance().getScenario(CutClean.class).isEnabled();
		ItemStack replaced = new ItemStack (cutclean ? Material.IRON_INGOT : Material.IRON_ORE);
    	
		if (block.getType() != Material.EMERALD_ORE && block.getType() != Material.REDSTONE_ORE && block.getType() != Material.LAPIS_ORE && block.getType() != Material.GOLD_ORE && block.getType() != Material.DIAMOND_ORE) {
			return;
    	}
		
        BlockUtils.blockBreak(player, block);
        BlockUtils.degradeDurabiliy(player);
        BlockUtils.dropItem(block.getLocation(), replaced);
        
        event.setCancelled(true);
        block.setType(Material.AIR);
    }
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		List<ItemStack> drops = event.getDrops();
		
		drops.add(new ItemStack (Material.STRING, 2));
		drops.add(new ItemStack (Material.DIAMOND, 1));
		drops.add(new ItemStack (Material.GOLDEN_APPLE, 1));
		drops.add(new ItemStack (Material.ARROW, 32));
	}
	
	@EventHandler
	public void onPrepareItemCraft(PrepareItemCraftEvent event) {
		ItemStack item = event.getRecipe().getResult();
		CraftingInventory inv = event.getInventory();
		
		if (item.getType() != Material.ANVIL && item.getType() != Material.GOLDEN_APPLE && item.getType() != Material.ENCHANTMENT_TABLE) {
			return;
		}
		
		inv.setResult(new ItemStack(Material.AIR));
	}
}