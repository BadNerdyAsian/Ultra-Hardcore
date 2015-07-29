package com.leontg77.uhc.scenario.types;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main;
import com.leontg77.uhc.scenario.Scenario;

public class GoToHell extends Scenario {
	private boolean enabled = false;
	private BukkitRunnable task;
	
	public GoToHell() {
		super("GoToHell", "After 45 minutes you have to be in the nether or else you take 0.5 hearts of damage every 30 seconds");
	}

	public void setEnabled(boolean enable) {
		enabled = enable;
		
		if (enable) {
			this.task = new BukkitRunnable() {
				public void run() {
					for (Player online : Bukkit.getServer().getOnlinePlayers()) {
						if (online.getWorld().getEnvironment() != Environment.NETHER) {
							online.damage(1.0);
						}
					}
				}
			};
			
			task.runTaskTimer(Main.plugin, 600, 600);
		} else {
			task.cancel();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}
}