package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PacketUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Bigcrack scenario class
 * 
 * @author dans1988
 */
public class BigCrack extends Scenario implements Listener, CommandExecutor {
	private static final int CHUNK_HEIGHT_LIMIT = 128;
    private static final int BLOCKS_PER_CHUNK = 16;

    public static final String PREFIX = "�7[�bBigcrack�7] �f";
	private boolean generation = false;

	public BigCrack() {
		super("BigCrack", "A Chunk Error running on the Z axis splits the world in half.");
		Bukkit.getPluginCommand("bigcrack").setExecutor(this);
	}
	
	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}

	@EventHandler
    public void onFlow(BlockFromToEvent event) {
        if (!generation) {
        	return;
        }
        
        event.setCancelled(true);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can generate cracks.");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!isEnabled()) {
			sender.sendMessage(PREFIX + "Bigcrack is not enabled.");
			return true;
		}
		
		if (!player.hasPermission("uhc.bigcrack")) {
			sender.sendMessage(Main.NO_PERM_MSG);
			return true;
		}
		
		if (args.length < 3) {
			player.sendMessage(PREFIX + "Usage: /bigcrack <width> <length> <speed>");
            return true;
        }

        int width;
        
        try {
            width = Integer.parseInt(args[0]);
        } catch (Exception e) {
        	player.sendMessage(ChatColor.RED + args[0] + " is not an vaild width.");
            return true;
        }

        int length;
        
        try {
            length = Integer.parseInt(args[1]);
        } catch (Exception e) {
        	player.sendMessage(ChatColor.RED + args[1] + " is not an vaild length.");
            return true;
        }

        int speed;
        
        try {
            speed = Integer.parseInt(args[2]);
        } catch (Exception e) {
        	player.sendMessage(ChatColor.RED + args[2] + " is not an vaild speed.");
            return true;
        }
        
        generate(player.getWorld(), length, width, speed);
		return true;
	}
	
	/**
	 * Generate the bigcrack.
	 * 
	 * @param world The world to use.
	 * @param length The length to use.
	 * @param width The width to use.
	 * @param speed The speed to use.
	 */
	public void generate(final World world, final int length, final int width, int speed) {
		generation = true;
        
        int xChunk;
        if (length % BLOCKS_PER_CHUNK == 0) {
            xChunk = length / BLOCKS_PER_CHUNK;
        } else {
            xChunk = (length / BLOCKS_PER_CHUNK) + 1;
        }

        int xMaxChunk = xChunk;
        xChunk = xChunk * -1;
        
        int zChunk;
        if (width % BLOCKS_PER_CHUNK == 0) {
            zChunk = (width) / BLOCKS_PER_CHUNK;
        } else {
            zChunk = ((width) / BLOCKS_PER_CHUNK) + 1;
        }
        
        int zMaxChunk = zChunk;
        zChunk = zChunk * -1;
        
        int delayMultiplier = 0;
        for (int x = xChunk; x <= xMaxChunk; x++) {
            for (int z = zChunk; z <= zMaxChunk; z++) {
                final Chunk chunk = world.getChunkAt(x, z);
                
                new BukkitRunnable() {
                    public void run() {
                        populate(world, chunk, width, length);
						
						for (Player online : PlayerUtils.getPlayers()) {
							PacketUtils.sendAction(online, PREFIX + "Populated chunk at x = �a" + chunk.getX() + "�7, z = �a" + chunk.getZ() + "�7.");
						}
                    }
                }.runTaskLater(Main.plugin, delayMultiplier * speed);
                
                delayMultiplier++;
            }
        }
        
        new BukkitRunnable() {
            public void run() {
            	generation = false;
                PlayerUtils.broadcast(PREFIX + "Bigcrack generation finished!");
            }
        }.runTaskLater(Main.plugin, delayMultiplier * speed);
    }

	/**
	 * Populate a chunk.
	 * 
	 * @param world The world of the crack.
	 * @param chunk The chunk to populate.
	 * @param length The length of the crack.
	 * @param width The width of the crack.
	 */
    public void populate(World world, Chunk chunk, int width, int length) {
        chunk.load();
        
        for (int x = 0; x < BLOCKS_PER_CHUNK; x++) {
            for (int z = 0; z < BLOCKS_PER_CHUNK; z++) {
                for (int y = CHUNK_HEIGHT_LIMIT - 1; y >= 0; y--) {
                	Block block = chunk.getBlock(x, y, z);
                    Location location = block.getLocation();
                    
                    int xLocation = location.getBlockX();
                    int zLocation = location.getBlockZ();
                    
                    if (zLocation >= (width * -1) && zLocation <= width && xLocation <= length && xLocation >= (length * -1)) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
}