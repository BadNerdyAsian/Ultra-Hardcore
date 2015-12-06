package com.leontg77.uhc.scenario.scenarios;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.utils.BlockUtils;

/**
 * Armageddon scenario class.
 * 
 * @author LeonTG77
 */
public class Armageddon extends Scenario implements Listener {
	private World world = Game.getInstance().getWorld();
	
	private long tickInterval = 50L;
	private int frequency = 5;
	private int task = -1;
	
	public Armageddon() {
		super("Armageddon", "Lava, gravel, potions, and nuggets fall from the sky.");
	}

	@Override
	public void onDisable() {
		if (task != -1) {
			Bukkit.getScheduler().cancelTask(task);
		}
		
		this.task = -1;
	}

	@Override
	public void onEnable() {
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				for (int i = 0; i < frequency; i++) {
					dropArmageddonItem();
				}
			}
		}, tickInterval, tickInterval);
		
		world = Game.getInstance().getWorld();
	}

	@SuppressWarnings("deprecation")
	private void dropArmageddonItem() {
		Chunk[] chunkscandidates = world.getLoadedChunks();
		Random rand = new Random();
		
		if (chunkscandidates.length == 0) {
			return;
		}
		
		Chunk dropChunk = chunkscandidates[rand.nextInt(chunkscandidates.length)];
		Location dropFrom = new Location(dropChunk.getWorld(), dropChunk.getX() * 16 + rand.nextInt(16), 255.0D, dropChunk.getZ() * 16 + rand.nextInt(15));
		
		handlePotionDrop(dropChunk, dropFrom);
		int randomInt = rand.nextInt(100);
		
		if (randomInt > 90) {
			int itemChoice = rand.nextInt(10);
			
			switch (itemChoice) {
			case 0:
				BlockUtils.dropItem(dropFrom, new ItemStack(Material.GOLD_INGOT, 1));
			case 1:
				BlockUtils.dropItem(dropFrom, new ItemStack(Material.DIAMOND, 1));
			case 2:
				BlockUtils.dropItem(dropFrom, new ItemStack(Material.NETHER_WARTS, 1));
			case 3:
				BlockUtils.dropItem(dropFrom, new ItemStack(Material.BLAZE_ROD, 1));
			case 4:
				BlockUtils.dropItem(dropFrom, new ItemStack(Material.STRING, 3));
			case 5:
				dropChunk.getWorld().spawnFallingBlock(dropFrom, Material.MELON_BLOCK, (byte) 0);
			case 6:
				dropChunk.getWorld().spawnFallingBlock(dropFrom, Material.IRON_BLOCK, (byte) 0);
			case 7:
				dropChunk.getWorld().spawnFallingBlock(dropFrom, Material.MOB_SPAWNER, (byte) 0);
			case 8:
				BlockUtils.dropItem(dropFrom, new ItemStack(Material.BLAZE_POWDER, 1));
			case 9:
				BlockUtils.dropItem(dropFrom, new ItemStack(Material.BOOK, 1));
			}
		} else if (randomInt > 70) {
			handlePotionDrop(dropChunk, dropFrom);
		} else if (randomInt > 40) {
			dropChunk.getWorld().spawnFallingBlock(dropFrom, rand.nextBoolean() ? Material.SAND : Material.GRAVEL, (byte) 0);
		} else {
			dropChunk.getWorld().spawnFallingBlock(dropFrom, Material.LAVA, (byte) 0);
		}
	}

	public void handlePotionDrop(Chunk dropChunk, Location dropFrom) {
		Random rand = new Random();
		int itemChoice = rand.nextInt(30);
		
		if (itemChoice > 20) {
			dropPotion(dropChunk, dropFrom, PotionType.INSTANT_DAMAGE, rand.nextInt(2));
		} else if (itemChoice > 15) {
			dropPotion(dropChunk, dropFrom, PotionType.POISON, rand.nextInt(2));
		} else if (itemChoice > 10) {
			dropPotion(dropChunk, dropFrom, PotionType.WEAKNESS, rand.nextInt(2));
		} else if (itemChoice > 5) {
			dropPotion(dropChunk, dropFrom, PotionType.SLOWNESS, rand.nextInt(2));
		} else {
			PotionType[] types = PotionType.values();
			dropPotion(dropChunk, dropFrom, types[rand.nextInt(types.length)], rand.nextInt(2));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void PotionSplashEvent(PotionSplashEvent event) {
		if (task == -1) {
			return;
		}
		
		LivingEntity source = (LivingEntity) event.getPotion().getShooter();
		
		if (source != null && source.toString() != null && source.toString().equalsIgnoreCase("CraftChicken")) {
			source.setHealth(0);
		}
	}

	private void dropPotion(Chunk dropChunk, Location dropFrom, PotionType type, int modifier) {
		if (type == PotionType.WATER) {
			return;
		}
		
		Potion potion = new Potion(type, 1 + modifier);
		potion.setSplash(true);
		
		if (potion.getType() != PotionType.INSTANT_DAMAGE && potion.getType() != PotionType.INSTANT_HEAL) {
			potion.extend();
		}
		
		ItemStack itemStack = potion.toItemStack(1);

		LivingEntity entity = (LivingEntity) dropChunk.getWorld().spawnEntity(dropFrom, EntityType.CHICKEN);
		ThrownPotion pot = (ThrownPotion) entity.launchProjectile(ThrownPotion.class, new Vector(0, -1, 0));
		
		pot.setItem(itemStack);
		pot.setVelocity(new Vector(0, -1, 0));
		
		if (pot.getItem().getType() == Material.AIR) {
			pot.remove();
		}
	}
}