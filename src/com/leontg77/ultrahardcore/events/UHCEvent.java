package com.leontg77.ultrahardcore.events;

import org.bukkit.event.Event;

import com.leontg77.ultrahardcore.Game;

/**
 * UHCEvent abstract class
 * <p>
 * Used to make all the subevents have the getGame() method.
 * 
 * @author LeonTG77
 */
public abstract class UHCEvent extends Event {

	/**
	 * Get the game instance.
	 * 
	 * @return The game instance.
	 */
	public Game getGame() {
		return Game.getInstance();
	}
}