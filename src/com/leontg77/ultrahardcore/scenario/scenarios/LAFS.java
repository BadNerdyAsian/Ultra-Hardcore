package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * LAFS scenario class
 * 
 * @author LeonTG77
 */
public class LAFS extends Scenario implements Listener {
	public static final String PREFIX = "�d�lLAFS �8� �7";

	public LAFS() {
		super("LAFS", "Stands for love at first sight, you team with the first player you see in the game, in order to get on a team with them right click the player.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		if (!(event.getRightClicked() instanceof Player)) {
			return;
		}
		
		Player clicked = (Player) event.getRightClicked();
		Player player = event.getPlayer();
		
		List<World> worlds = GameUtils.getGameWorlds();
		TeamManager teams = TeamManager.getInstance();
		
		if (!worlds.contains(player.getWorld()) && !worlds.contains(clicked.getWorld())) {
			return;
		}
		
		if (teams.getTeam(player) != null) {
			player.sendMessage(ChatColor.RED + "You are already on a team");
			return;
		}
		
		if (teams.getTeam(clicked) != null) {
			player.sendMessage(ChatColor.RED + "That player is already on a team.");
			return;
		}
		
		Team team = teams.findAvailableTeam();
		
		if (team == null) {
			clicked.sendMessage(ChatColor.RED + "There are no more teams for you to join.");
			player.sendMessage(ChatColor.RED + "There are no more teams for you to join.");
			return;
		}

		teams.joinTeam(team, clicked);
		teams.joinTeam(team, player);
		
		PlayerUtils.broadcast(PREFIX + "�a" + player.getName() + " �7and�a " + clicked.getName() + " �7has found each other.");
	}
}