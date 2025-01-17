package com.leontg77.ultrahardcore.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Game;

/**
 * Super class for commands.
 * 
 * @author LeonTG77
 */
public abstract class UHCCommand extends Parser {
	private String name, usage;
	protected Game game;
	
	/**
	 * Constructor for the uhc command super class.
	 * 
	 * @param name The name of the command.
	 * @param usage the command usage (after /command)
	 */
	public UHCCommand(String name, String usage) {
		this.usage = usage;
		this.name = name;
		
		this.game = Game.getInstance();
	}
	
	/**
	 * Get the name of the command used after the /
	 * 
	 * @return The command name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the usage of the command
	 * <p>
	 * Usage can be /nameofcommand [argurments...]
	 * 
	 * @return The command usage.
	 */
	public String getUsage() {
		return "/" + name + " " + usage;
	}
	
	/**
	 * Return the permission of the command
	 * <p>
	 * The permission will be uhc.[nameofcommand]
	 * 
	 * @return The command permission.
	 */
	public String getPermission() {
		return "uhc." + name;
	}
	
	/**
	 * Execute the command.
	 * 
	 * @param sender The sender of the command.
	 * @param args The argurments typed after the command.
	 * @return True if successful, false otherwise. Returning false will send usage to the sender.
	 * 
	 * @throws CommandException If anything was wrongly typed this is thrown sending the sender a warning.
	 */
	public abstract boolean execute(CommandSender sender, String[] args) throws CommandException;
	
	/**
	 * Tab complete the command.
	 * 
	 * @param sender The sender of the command.
	 * @param args The argurments typed after the command
	 * @return A list of tab completable argurments.
	 */
	public abstract List<String> tabComplete(CommandSender sender, String[] args);
	
	/**
	 * Turn a the given boolean into "Enabled" or "Disabled".
	 * 
	 * @param converting The boolean converting.
	 * @return The converted boolean.
	 */
	public String booleanToString(boolean converting) {
		return converting ? "enabled" : "disabled";
	}
}