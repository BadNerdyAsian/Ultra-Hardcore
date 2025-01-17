package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Permakill scenario class
 * 
 * @author LeonTG77
 */
public class Permakill extends Scenario implements Listener {

	public Permakill() {
		super("Permakill", "Everytime a player dies it toggles between perma day and perma night");
	}

	@Override
	public void onDisable() {
		Game game = Game.getInstance();
		game.getWorld().setGameRuleValue("doDaylightCycle", "true");
	}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Game game = Game.getInstance();
		
		if (game.getWorld().getTime() == 6000) {
			game.getWorld().setTime(18000);
		} else {
			game.getWorld().setTime(6000);
		}
	}
	
	@EventHandler
	public void on(GameStartEvent event) {
		Game game = Game.getInstance();
		
		game.getWorld().setGameRuleValue("doDaylightCycle", "false");
		game.getWorld().setTime(6000);
	}
}