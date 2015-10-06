package com.leontg77.uhc;

import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Announcer class.
 * <p>
 * This class contains methods for sending announcements to the server every minute.
 * 
 * @author LeonTG77
 */
public class Announcer {
	private static Announcer instance = new Announcer();
	private BukkitRunnable task;
	
	/**
	 * Gets the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Announcer getInstance() {
		return instance;
	}
	
	/**
	 * Setup the arena class.
	 */
	public void setup() {
		task = new BukkitRunnable() {
			public void run() {
				PlayerUtils.broadcast("�8(�6!�8)�7 " + getRandomAnnouncement());
			}
		};
		
		task.runTaskTimer(Main.plugin, 12000, 12000);
		
		Main.plugin.getLogger().info("The announcer has been setup.");
	}

	private String getRandomAnnouncement() {
		return "No announcements setup.";
	}
}