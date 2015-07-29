package com.leontg77.uhc.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.leontg77.uhc.Arena;
import com.leontg77.uhc.GameState;
import com.leontg77.uhc.InvGUI;
import com.leontg77.uhc.Main;
import com.leontg77.uhc.Scoreboards;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.Spectator;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.util.BlockUtils;
import com.leontg77.uhc.util.PlayerUtils;
import com.leontg77.uhc.util.RecipeUtils;
import com.leontg77.uhc.util.ServerUtils;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {
	private Settings settings = Settings.getInstance();
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		event.setJoinMessage(null);
		
		Spectator.getManager().hideAll(player);
		PlayerUtils.setTabList(player);
		
		player.setFlySpeed(0.1f);
		player.setWalkSpeed(0.2f);
		
		if (Main.relog.containsKey(player.getName())) {
			Main.relog.get(player.getName()).cancel();
			Main.relog.remove(player.getName());
		}
		
		if (!TeamCommand.invites.containsKey(player)) {
			TeamCommand.invites.put(player, new ArrayList<Player>());
		}
		
		if (Main.spectating.contains(player.getName())) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			
			Spectator.getManager().set(player, true);
		}
		
		if (GameState.isState(GameState.INGAME) && !player.isWhitelisted()) {
			player.sendMessage(Main.prefix() + "You joined in a game that you didn't play or died in.");
			player.sendMessage(Main.prefix() + "Spectator mode is now enabled because of that.");
			
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.setExp(0);
			
			Spectator.getManager().set(player, true);
		}
		
		if (!Main.spectating.contains(player.getName())) {
			PlayerUtils.broadcast("�8[�a+�8] �7" + player.getName() + " has joined.");
		}
		
		if (GameState.isState(GameState.LOBBY)) {
			World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
			double x = settings.getData().getDouble("spawn.x");
			double y = settings.getData().getDouble("spawn.y");
			double z = settings.getData().getDouble("spawn.z");
			float yaw = (float) settings.getData().getDouble("spawn.yaw");
			float pitch = (float) settings.getData().getDouble("spawn.pitch");
			final Location loc = new Location(w, x, y, z, yaw, pitch);
			player.teleport(loc);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		event.setQuitMessage(null);
		
		if (!Main.spectating.contains(player.getName())) {
			PlayerUtils.broadcast("�8[�c-�8] �7" + player.getName() + " has left.");
			
			if (!GameState.isState(GameState.LOBBY)) {
				Main.relog.put(player.getName(), new BukkitRunnable() {
					public void run() {
						if (!player.isOnline()) {
							if (player.isWhitelisted()) {
								player.setWhitelisted(false);
								Scoreboards.getManager().resetScore(player.getName());
								Main.relog.remove(player.getName());
								
								for (ItemStack content : player.getInventory().getContents()) {
									if (content != null) {
										Item item = player.getWorld().dropItem(player.getLocation().add(0.5, 0.7, 0.5), content);
										item.setVelocity(new Vector(0, 0.2, 0));
									}
								}

								for (ItemStack armorContent : player.getInventory().getArmorContents()) {
									if (armorContent != null && armorContent.getType() != Material.AIR) {
										Item item = player.getWorld().dropItem(player.getLocation().add(0.5, 0.7, 0.5), armorContent);
										item.setVelocity(new Vector(0, 0.2, 0));
									}
								}
								
								ExperienceOrb exp = player.getWorld().spawn(player.getLocation().add(0.5, 0.7, 0.5), ExperienceOrb.class);
								exp.setExperience((int) player.getExp());
								exp.setVelocity(new Vector(0, 0.2, 0));
								
								player.getInventory().clear();
								player.getInventory().setArmorContents(null);
								player.setExp(0);
								
								PlayerUtils.broadcast(Main.prefix(ChatColor.GREEN) + player.getName() + " �7took too long to come back.");
								PlayerDeathEvent event = new PlayerDeathEvent(player, new ArrayList<ItemStack>(), 0, null);
								Bukkit.getServer().getPluginManager().callEvent(event);
							}
						}
					}
				});
				
				Main.relog.get(player.getName()).runTaskLater(Main.plugin, 600);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Player player = event.getEntity().getPlayer();
		
		if (!Arena.getManager().isEnabled()) {
			player.setWhitelisted(false);
		    player.getWorld().strikeLightningEffect(player.getLocation());

		    if (Main.ghead) {
				try {
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							player.getLocation().getBlock().setType(Material.NETHER_FENCE);
					        player.getLocation().add(0, 1, 0).getBlock().setType(Material.SKULL);
					        
					        Skull skull = (Skull) player.getLocation().add(0, 1, 0).getBlock().getState();
						    skull.setSkullType(SkullType.PLAYER);
						    skull.setOwner(player.getName());
						    skull.setRotation(BlockUtils.getBlockFaceDirection(player.getLocation()));
						    skull.update();
						    
						    Block b = player.getLocation().add(0, 1, 0).getBlock();
						    b.setData((byte) 0x1, true);
						}
					}, 1L);
				} catch (Exception e) {
					Bukkit.getLogger().warning(ChatColor.RED + "Could not place player skull.");
				}
		    }

			if (event.getEntity().getKiller() == null) {
		        Scoreboards.getManager().setScore("�a�lPvE", Scoreboards.getManager().getScore("�a�lPvE") + 1);
				Scoreboards.getManager().resetScore(player.getName());
				return;
			}
			
			Player killer = event.getEntity().getKiller();

	        Scoreboards.getManager().setScore(killer.getName(), Scoreboards.getManager().getScore(killer.getName()) + 1);
			Scoreboards.getManager().resetScore(player.getName());
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		
		World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world"));
		double x = settings.getData().getDouble("spawn.x");
		double y = settings.getData().getDouble("spawn.y");
		double z = settings.getData().getDouble("spawn.z");
		float yaw = (float) settings.getData().getDouble("spawn.yaw");
		float pitch = (float) settings.getData().getDouble("spawn.pitch");
		
		Location loc = new Location(w, x, y, z, yaw, pitch);
		event.setRespawnLocation(loc);
		
		if (!Arena.getManager().isEnabled() && !GameState.isState(GameState.LOBBY)) {
			player.sendMessage(Main.prefix() + "�7Thanks for playing our game, it really means a lot :)");
			if (player.hasPermission("uhc.prelist")) {
				player.sendMessage("�8�l� �7You will be put into spectator mode in 30 seconds. (No spoiling please)");
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						if (!GameState.isState(GameState.LOBBY) && player.isOnline() && !Main.spectating.contains(player.getName())) {
							Spectator.getManager().set(player, true);
						}
					}
				}, 600);
			} else {
				player.sendMessage("�8�l� �7You have 30 seconds to say your goodbyes. (No spoiling please)");
				
				Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, new Runnable() {
					public void run() {
						if (!GameState.isState(GameState.LOBBY) && player.isOnline()) {
							player.kickPlayer("�8�l� �7Thanks for playing! �8�l�");
						}
					}
				}, 600);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		
		if (VoteCommand.vote) {
			if (event.getMessage().equalsIgnoreCase("y")) {
				if (!GameState.isState(GameState.LOBBY) && player.getWorld().getName().equals("lobby")) {
					player.sendMessage(ChatColor.RED + "You cannot vote when you are dead.");
					event.setCancelled(true);
					return;
				}
				
				if (Main.spectating.contains(player.getName())) {
					player.sendMessage(ChatColor.RED + "You cannot vote as a spectator.");
					event.setCancelled(true);
					return;
				}
				
				if (Main.voted.contains(player.getName())) {
					player.sendMessage(ChatColor.RED + "You have already voted.");
					event.setCancelled(true);
					return;
				}
				player.sendMessage(Main.prefix() + "Vote voted yes.");
				VoteCommand.yes++;
				event.setCancelled(true);
				Main.voted.add(player.getName());
				return;
			}
			
			if (event.getMessage().equalsIgnoreCase("n")) {
				if (!GameState.isState(GameState.LOBBY) && player.getWorld().getName().equals("lobby")) {
					player.sendMessage(ChatColor.RED + "You cannot vote when you are dead.");
					event.setCancelled(true);
					return;
				}
				
				if (Main.spectating.contains(player.getName())) {
					player.sendMessage(ChatColor.RED + "You cannot vote as a spectator.");
					event.setCancelled(true);
					return;
				}
				
				if (Main.voted.contains(player.getName())) {
					player.sendMessage(ChatColor.RED + "You have already voted.");
					event.setCancelled(true);
					return;
				}
				player.sendMessage(Main.prefix() + "You voted no.");
				VoteCommand.no++;
				event.setCancelled(true);
				Main.voted.add(player.getName());
				return;
			}
		}
    	
		if (PermissionsEx.getUser(player).inGroup("Host")) {
			Team team = player.getScoreboard().getEntryTeam(player.getName());
			if (player.getUniqueId().toString().equals("02dc5178-f7ec-4254-8401-1a57a7442a2f")) {
				if (settings.getData().getString("game.host").equals(player.getName())) {
					PlayerUtils.broadcast("�3�lHost �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
				} else {
					if (Main.mute.contains("a")) {
						player.sendMessage(Main.prefix() + "All players are muted.");
						event.setCancelled(true);
						return;
					}
					if (Main.mute.contains(player.getName())) {
						player.sendMessage(Main.prefix() + "You have been muted.");
						event.setCancelled(true);
						return;
					}

					PlayerUtils.broadcast("�3�lCo-Host �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
				}
			} else {
				if (settings.getData().getString("game.host").equals(player.getName())) {
					PlayerUtils.broadcast("�4�lHost �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
				} else {
					if (Main.mute.contains("a")) {
						player.sendMessage(Main.prefix() + "All players are muted.");
						event.setCancelled(true);
						return;
					}
					if (Main.mute.contains(player.getName())) {
						player.sendMessage(Main.prefix() + "You have been muted.");
						event.setCancelled(true);
						return;
					}

					PlayerUtils.broadcast("�4�lCo-Host �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
				}		
			}
		}
		else if (PermissionsEx.getUser(player).inGroup("Staff")) {
			if (Main.mute.contains("a")) {
				player.sendMessage(Main.prefix() + "All players are muted.");
				event.setCancelled(true);
				return;
			}
			if (Main.mute.contains(player.getName())) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				event.setCancelled(true);
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());
			PlayerUtils.broadcast("�c�lStaff �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
		}
		else if (PermissionsEx.getUser(player).inGroup("VIP")) {
			if (Main.mute.contains("a")) {
				player.sendMessage(Main.prefix() + "All players are muted.");
				event.setCancelled(true);
				return;
			}
			if (Main.mute.contains(player.getName())) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				event.setCancelled(true);
				return;
			}

			Team team = player.getScoreboard().getEntryTeam(player.getName());

			PlayerUtils.broadcast("�5�lVIP �8| �f" + (team != null ? (team.getName().equals("spec") ? player.getName() : team.getPrefix() + player.getName()) : player.getName()) + "�8 � �f" + event.getMessage());
		} 
		else {
			if (Main.mute.contains("a")) {
				player.sendMessage(Main.prefix() + "All players are muted.");
				event.setCancelled(true);
				return;
			}
			if (Main.mute.contains(player.getName())) {
				player.sendMessage(Main.prefix() + "You have been muted.");
				event.setCancelled(true);
				return;
			}
			Team team = player.getScoreboard().getEntryTeam(player.getName());

			PlayerUtils.broadcast((team != null ? team.getPrefix() + player.getName() : player.getName()) + "�8 � �f" + event.getMessage());
		} 
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		
		for (Player online : PlayerUtils.getPlayers()) {
			if (online.hasPermission("uhc.commandspy") && (online.getGameMode() == GameMode.CREATIVE || Main.spectating.contains(online.getName())) && online != player) {
				online.sendMessage(ChatColor.YELLOW + player.getName() + ": �7" + event.getMessage());
			}
		}
		
		if (event.getMessage().split(" ")[0].startsWith("/me")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have access to that command.");
		}
		
		if (event.getMessage().split(" ")[0].startsWith("/bukkit:") && !player.hasPermission("uhc.admin")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have access to that command.");
		}
		
		if (event.getMessage().split(" ")[0].startsWith("/minecraft:") && !player.hasPermission("uhc.admin")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have access to that command.");
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/kill")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You do not have access to that command.");
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/rl")) {
			if (!GameState.isState(GameState.LOBBY)) {
				player.sendMessage(ChatColor.RED + "You might not want to reload when the game is running.");
				player.sendMessage(ChatColor.RED + "If you still want to reload, do it in the console.");
				event.setCancelled(true);
			}
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/reload")) {
			if (!GameState.isState(GameState.LOBBY)) {
				player.sendMessage(ChatColor.RED + "You might not want to reload when the game is running.");
				player.sendMessage(ChatColor.RED + "If you still want to reload, do it in the console.");
				event.setCancelled(true);
			}
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/border3000")) {
			if (player.hasPermission("uhc.border")) {
				player.getWorld().getWorldBorder().setSize(2999);
				if (player.getWorld().getEnvironment() == Environment.NETHER) {
					player.getWorld().getWorldBorder().setCenter(0, 0);
				} else {
					player.getWorld().getWorldBorder().setCenter(0.5, 0.5);
				}
				player.getWorld().getWorldBorder().setWarningDistance(0);
				player.getWorld().getWorldBorder().setWarningTime(60);
				player.getWorld().getWorldBorder().setDamageAmount(0.1);
				player.getWorld().getWorldBorder().setDamageBuffer(50);
				PlayerUtils.broadcast(Main.prefix() + "Border setup with radius of 3000x3000.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/border2000")) {
			if (player.hasPermission("uhc.border")) {
				player.getWorld().getWorldBorder().setSize(1999);
				if (player.getWorld().getEnvironment() == Environment.NETHER) {
					player.getWorld().getWorldBorder().setCenter(0, 0);
				} else {
					player.getWorld().getWorldBorder().setCenter(0.5, 0.5);
				}
				player.getWorld().getWorldBorder().setWarningDistance(0);
				player.getWorld().getWorldBorder().setWarningTime(60);
				player.getWorld().getWorldBorder().setDamageAmount(0.1);
				player.getWorld().getWorldBorder().setDamageBuffer(50);
				PlayerUtils.broadcast(Main.prefix() + "Border setup with radius of 2000x2000.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
		
		if (event.getMessage().split(" ")[0].equalsIgnoreCase("/perma")) {
			if (player.hasPermission("uhc.perma")) {
				player.getWorld().setGameRuleValue("doDaylightCycle", "false");
				player.getWorld().setTime(6000);
				PlayerUtils.broadcast(Main.prefix() + "Permaday enabled.");
			} else {
				player.sendMessage(ChatColor.RED + "You do not have access to that command.");
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();

		if (event.getResult() == Result.KICK_BANNED) {
			if (player.hasPermission("uhc.staff")) {
				event.allow();
				return;
			}

			event.setKickMessage("�8�l� �cBanned: �7" + Bukkit.getBanList(Type.NAME).getBanEntry(player.getName()).getReason() + " �8�l�");
			return;
		}
		
		if (event.getResult() == Result.KICK_WHITELIST) {
			if (player.hasPermission("uhc.prelist")) {
				event.allow();
				return;
			}
			
			event.setKickMessage("�8�l� �7You are not whitelisted �8�l�");
			return;
		}
		
		if (PlayerUtils.getPlayers().size() == settings.getData().getInt("maxplayers")) {
			if (player.hasPermission("uhc.prelist")) {
				event.allow();
				return;
			} 
			event.disallow(Result.KICK_FULL, "�8�l� �7The server is full �8�l�");
		} else {
			event.allow();
		}
	}
	
	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		String bm = ChatColor.translateAlternateColorCodes('&', settings.getData().getString("motd"));
		
		if (bm == null) {
			event.setMotd(Bukkit.getMotd());
		} else {
			StringBuilder s = new StringBuilder();
			
			for (String st : bm.split(" ")) {
				s.append("�6" + st + " ");
			}
			
			event.setMotd("�4�lUltra Hardcore �8- �71.8 �8- �a" + ServerUtils.getState() + "�r \n" + 
			s.toString().trim() + "�8 - �4Host: �7" + Settings.getInstance().getData().getString("game.host"));
		}

		int max = settings.getData().getInt("maxplayers");
		
		if (max == 0) {
			event.setMaxPlayers(Bukkit.getMaxPlayers());
		} else {
			event.setMaxPlayers(max);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {	
        Player player = event.getPlayer();
		
		if (!Main.spectating.contains(player.getName())) {
			return;
		}
        
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			InvGUI.getManager().openSelector(player);
		} 
		else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			ArrayList<Player> players = new ArrayList<Player>();
			for (Player online : Bukkit.getServer().getOnlinePlayers()) {
				if (!Main.spectating.contains(online.getName())) {
					players.add(online);
				}
			}
			
			if (players.size() > 0) {
				Player target = players.get(new Random().nextInt(players.size()));
				player.teleport(target.getLocation());
				player.sendMessage(Main.prefix() + "You teleported to �a" + target.getName() + "�7.");
			} else {
				player.sendMessage(Main.prefix() + "No players to teleport to.");
			}
		}
	}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {	
        if (event.getCurrentItem() == null) {
        	return;
        }
        
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		if (!Main.spectating.contains(player.getName())) {
			return;
		}
        
		if (event.getInventory().getTitle().equals("Player Selector")) {
			Player target = Bukkit.getServer().getPlayer(event.getCurrentItem().getItemMeta().getDisplayName().substring(2, event.getCurrentItem().getItemMeta().getDisplayName().length()));
			
			if (target != null) {
				player.teleport(target);
			}
			
			event.setCancelled(true);
		}
		
		if (item.getType() == Material.COMPASS) {
			if (event.isLeftClick()) {
				ArrayList<Player> players = new ArrayList<Player>();
				for (Player online : Bukkit.getServer().getOnlinePlayers()) {
					if (!Main.spectating.contains(online.getName())) {
						players.add(online);
					}
				}
				
				if (players.size() > 0) {
					Player target = players.get(new Random().nextInt(players.size()));
					player.teleport(target.getLocation());
					player.sendMessage(Main.prefix() + "You teleported to �a" + target.getName() + "�7.");
				} else {
					player.sendMessage(Main.prefix() + "No players to teleport to.");
				}
				event.setCancelled(true);
				return;
			}
			InvGUI.getManager().openSelector(player);
			event.setCancelled(true);
		}
		
		if (item.getType() == Material.INK_SACK) {
			if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
				player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			} else {
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000000, 0));
			}
			event.setCancelled(true);
		}
		
		if (item.getType() == Material.FEATHER) {
			player.teleport(new Location(player.getWorld(), 0, 100, 0));
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (Main.invsee.containsKey(event.getInventory())) {
			Main.invsee.get(event.getInventory()).cancel();
			Main.invsee.remove(event.getInventory());
		}
	}
	
	@EventHandler
    public void onPlayerInteractPlayer(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Player)) {
			return;
		}
		
    	Player player = (Player) event.getPlayer();
    	Player clicked = (Player) event.getRightClicked();
				
		if (Main.spectating.contains(player.getName())) {
			if (Main.spectating.contains(clicked.getName())) {
				return;
			}
			
			InvGUI.getManager().openInv(player, clicked);
		}
    }
	
	@EventHandler
	public void onPreCraftEvent(PrepareItemCraftEvent event) {
		ItemStack item = event.getRecipe().getResult();
		
        if (RecipeUtils.areSimilar(event.getRecipe(), Main.res)) {
            ItemMeta meta = item.getItemMeta();
            String name = "N/A";
          
            for (ItemStack items : event.getInventory().getContents()) {
                if (items.getType() == Material.SKULL_ITEM) {
                    SkullMeta skullMeta = (SkullMeta) items.getItemMeta();
                    name = skullMeta.getOwner();
                }
            }

            List<String> list = meta.getLore();
            list.add(ChatColor.AQUA + "Made from the head of: " + name);
            meta.setLore(list);
            item.setItemMeta(meta);
            event.getInventory().setResult(item);
        }
		
		if (item != null && item.getType() == Material.GOLDEN_APPLE) {
			if (item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals("�6Golden Head")) {
				if (ScenarioManager.getManager().getScenario("VengefulSpirits").isEnabled()) {
					return;
				}
				
				if (!Main.ghead) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
			
			if (item.getDurability() == 1) {
				if (!Main.godapple) {
					event.getInventory().setResult(new ItemStack(Material.AIR));
				}
			}
		}
    }
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		final Player player = event.getPlayer();
		final float before = player.getSaturation();
		
		new BukkitRunnable() {
			public void run() {
				float change = player.getSaturation() - before;
				player.setSaturation((float) (before + change * 2.5D));
			}
	    }.runTaskLater(Main.plugin, 1L);
		
		if (!Main.absorption) {
			if (event.getItem().getType() == Material.GOLDEN_APPLE) {
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
					public void run() {
						player.removePotionEffect(PotionEffectType.ABSORPTION);
					}
		        }, 1L);
			}
		}
		
		if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getItemMeta().getDisplayName() != null && event.getItem().getItemMeta().getDisplayName().equals("�6Golden Head")) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
		}
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		Player player = (Player) event.getEntity();
		
		if (player.getWorld().getName().equals("lobby")) {
			event.setCancelled(true);
			event.setFoodLevel(20);
			return;
		}
		
		if (event.getFoodLevel() < player.getFoodLevel()) {
			event.setCancelled(new Random().nextInt(100) < 66);
	    }
	}
	
	@EventHandler
	public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
		Player player = event.getPlayer();
		
		if (Main.spectating.contains(player.getName())) {
			event.setCancelled(true);
		}
		
		if (player.getWorld().getName().equals("lobby")) {
			event.setCancelled(true);
		}
		
		if (player.getWorld().getName().equals("arena")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (GameState.isState(GameState.WAITING)) {
			event.setCancelled(true);
		}
	}
}