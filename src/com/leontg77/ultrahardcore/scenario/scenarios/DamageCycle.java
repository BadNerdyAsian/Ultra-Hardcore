package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.scenarios.PeriodOfResistance.DamageType;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * DamageCycle scenario class
 * 
 * @author LeonTG77
 */
public class DamageCycle extends Scenario implements Listener, CommandExecutor {
	private static final String PREFIX = "§7[§8DamageCycle§7] ";
	private static final int DAMAGE_AMOUNT = 10000;
	
	private BukkitRunnable task;
	private DamageType current;
	
	private int seconds = 600;
	
	public DamageCycle() {
		super("DamageCycle", "Every 10 minutes the damage type changes, during the next 10 minutes if you take damage from that type you die.");
		
		Bukkit.getPluginCommand("current").setExecutor(this);
	}

	@Override
	public void onDisable() {
		if (task != null && Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
			task.cancel();
			task = null;
		}
		
		current = null;
		seconds = 600;
	}

	@Override
	public void onEnable() {
		seconds = 1;
		
		if (!State.isState(State.INGAME)) {
			return;
		}

		on(new GameStartEvent());
	}
	
	@EventHandler
	public void on(GameStartEvent event) {
		task = new BukkitRunnable() {
			private List<DamageType> types = ImmutableList.copyOf(Arrays.asList(DamageType.values()));
			private Random rand = new Random();
			
			public void run() {
				seconds--;
				
				switch (seconds) {
	            case 300:
	                PlayerUtils.broadcast(PREFIX + "Changing damage type in 5 minutes!");
	                break;
	            case 60:
	                PlayerUtils.broadcast(PREFIX + "Changing damage type in 1 minute!");
	                break;
	            case 30:
	            case 10:
	            case 5:
	            case 4:
	            case 3:
	            case 2:
	                PlayerUtils.broadcast(PREFIX + "Changing damage type in " + seconds + " seconds!");
	                break;
	            case 1:
	                PlayerUtils.broadcast(PREFIX + "Changing damage type in 1 second!");
	                break;
	            case 0:
	        		current = types.get(rand.nextInt(types.size()));
	                PlayerUtils.broadcast(PREFIX + "§6All damage from §7" + current.name().toLowerCase().replaceAll("_", " ") + "§6 will now instant kill you!");

	                seconds = 600;
	                break;
				}
			}
		};
		
		task.runTaskTimer(Main.plugin, 0, 20);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void on(EntityDamageEvent event) {
		// should only work for players.
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		if (event instanceof EntityDamageByEntityEvent) {
			on((EntityDamageByEntityEvent) event);
		}
		
		switch (event.getCause()) {
		case BLOCK_EXPLOSION:
		case ENTITY_EXPLOSION:
			if (current == DamageType.EXPLOSIONS) {
				event.setDamage(DAMAGE_AMOUNT);
			}
			break;
		case DROWNING:
			if (current == DamageType.DROWNING) {
				event.setDamage(DAMAGE_AMOUNT);
			}
			break;
		case FALL:
			if (current == DamageType.FALLING) {
				event.setDamage(DAMAGE_AMOUNT);
			}
			break;
		case FIRE:
		case FIRE_TICK:
		case LAVA:
			if (current == DamageType.LAVA_AND_FIRE) {
				event.setDamage(DAMAGE_AMOUNT);
			}
			break;
		case POISON:
			if (current == DamageType.POISON) {
				event.setDamage(DAMAGE_AMOUNT);
			}
			break;
		case STARVATION:
			if (current == DamageType.STARVATION) {
				event.setDamage(DAMAGE_AMOUNT);
			}
			break;
		case SUFFOCATION:
			if (current == DamageType.SUFFOCATION) {
				event.setDamage(DAMAGE_AMOUNT);
			}
			break;
		default:
			break;
		}
	}
	
	private void on(EntityDamageByEntityEvent event) {
		// again, only players.
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		
		Entity damager = event.getDamager();
		
		switch (damager.getType()) {
		case ZOMBIE:
			if (current == DamageType.ZOMBIES) {
				event.setDamage(DAMAGE_AMOUNT);
			}
			break;
		case SPIDER:
		case CAVE_SPIDER:
			if (current == DamageType.SPIDERS) {
				event.setDamage(DAMAGE_AMOUNT);
			}
			break;
		case ARROW:
			if (((Arrow) damager).getShooter() instanceof Skeleton && current == DamageType.SKELETONS) {
				event.setDamage(DAMAGE_AMOUNT);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "DamageCycle is not enabled.");
			return true;
		}
		
		if (current == null) {
			sender.sendMessage(PREFIX + "§6No damage type has been set yet.");
			return true;
		}
		
		sender.sendMessage(PREFIX + "§6All damage from §7" + current.name().toLowerCase().replaceAll("_", " ") + "§6 will instant kill you!");
		sender.sendMessage(PREFIX + "§6The damage type changes in §7" + DateUtils.ticksToString(seconds) + "§6.");
		return true;
	}
}