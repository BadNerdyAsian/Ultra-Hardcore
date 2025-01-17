package com.leontg77.ultrahardcore.commands.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.inventory.InvGUI;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Hall of fame command class.
 * 
 * @author LeonTG77
 */
public class HOFCommand extends UHCCommand {

	public HOFCommand() {
		super("hof", "[host]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		String host = GameUtils.getHostName(game.getHost());
		Settings settings = Settings.getInstance();
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("global")) {
				int matchCount = 0;
				
				for (String hostName : settings.getHOF().getKeys(false)) {
					matchCount += settings.getHOF().getConfigurationSection(hostName + ".games").getKeys(false).size();
				}
				
				sender.sendMessage(Main.PREFIX + "There's been a total of �a" + matchCount + " �7games hosted here.");
				return true;
			}
			
			host = GameUtils.getHostName(args[0]);
		}
		
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can view the hall of fame.");
		}
		
		Player player = (Player) sender;
		
		if (settings.getHOF().getConfigurationSection(host) == null) {
			throw new CommandException("'" + host + "' has never hosted any games here.");
		}
		
		InvGUI inv = InvGUI.getInstance();
		inv.openHOF(player, host);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		Settings settings = Settings.getInstance();
    	
		if (args.length == 1) {
    		for (Player online : PlayerUtils.getPlayers()) {
    			Rank rank = User.get(online).getRank();
    			
    			if (rank.getLevel() > 4) {
    				toReturn.add(online.getName());
    			}
    		}
    		
    		for (String host : settings.getHOF().getKeys(false)) {
				toReturn.add(host);
    		}
    		
			toReturn.add("global");
        }
		
    	return toReturn;
	}
}