package com.leontg77.uhc.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.Game;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.managers.BoardManager;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * Board command class.
 * 
 * @author LeonTG77
 */
public class BoardCommand implements CommandExecutor {	

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("uhc.board")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		BoardManager score = BoardManager.getInstance();
		Game game = Game.getInstance();
		
		if (game.pregameBoard()) {
			for (String entry : score.board.getEntries()) {
				score.resetScore(entry);
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Cleared pregame board.");
			game.setPregameBoard(false);
			return true;
		}
		
		for (String entry : score.board.getEntries()) {
			score.resetScore(entry);
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "Generated pregame board.");
		game.setPregameBoard(true);

		if (game.teamManagement()) {
			score.setScore("�e ", 14);
			score.setScore("�8� �cTeam:", 13);
			score.setScore("�8� �7/team", 12);
		}
		
		if (Arena.getInstance().isEnabled()) {
			score.setScore("�a ", 11);
			score.setScore("�8� �cArena:", 10);
			score.setScore("�8� �7/a ", 9);
		}
		
		if (!GameUtils.getTeamSize().isEmpty()) {
			score.setScore("�b ", 8);
			score.setScore("�8� �cTeamsize:", 7);
			score.setScore("�8� �7" + GameUtils.getAdvancedTeamSize(), 6);
		}
		
		score.setScore("�c ", 5);
		score.setScore("�8� �cScenarios:", 4);
		
		for (String scen : game.getScenarios().split(" ")) {
			score.setScore("�8� �7" + scen, 3);
		}
		
		score.setScore("�d ", 2);
		score.setScore("�8�m------------", 1);
		score.setScore("�a�o@ArcticUHC", 1);
		score.setScore("�a�o@ArcticUHC", 0);
		return true;
	}
}