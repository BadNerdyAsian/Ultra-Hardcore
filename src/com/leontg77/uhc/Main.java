package com.leontg77.uhc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.comphenix.protocol.PacketType.Play;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.leontg77.uhc.Spectator.SpecInfo;
import com.leontg77.uhc.cmds.ArenaCommand;
import com.leontg77.uhc.cmds.BanCommand;
import com.leontg77.uhc.cmds.BanIPCommand;
import com.leontg77.uhc.cmds.BoardCommand;
import com.leontg77.uhc.cmds.BorderCommand;
import com.leontg77.uhc.cmds.BroadcastCommand;
import com.leontg77.uhc.cmds.ButcherCommand;
import com.leontg77.uhc.cmds.ChatCommand;
import com.leontg77.uhc.cmds.ClearInvCommand;
import com.leontg77.uhc.cmds.ClearXpCommand;
import com.leontg77.uhc.cmds.ConfigCommand;
import com.leontg77.uhc.cmds.EditCommand;
import com.leontg77.uhc.cmds.EndCommand;
import com.leontg77.uhc.cmds.FeedCommand;
import com.leontg77.uhc.cmds.FireCommand;
import com.leontg77.uhc.cmds.FlyCommand;
import com.leontg77.uhc.cmds.GamemodeCommand;
import com.leontg77.uhc.cmds.GiveCommand;
import com.leontg77.uhc.cmds.GiveallCommand;
import com.leontg77.uhc.cmds.HOFCommand;
import com.leontg77.uhc.cmds.HealCommand;
import com.leontg77.uhc.cmds.HealthCommand;
import com.leontg77.uhc.cmds.HelpopCommand;
import com.leontg77.uhc.cmds.HotbarCommand;
import com.leontg77.uhc.cmds.InfoCommand;
import com.leontg77.uhc.cmds.InvseeCommand;
import com.leontg77.uhc.cmds.KickCommand;
import com.leontg77.uhc.cmds.ListCommand;
import com.leontg77.uhc.cmds.MatchpostCommand;
import com.leontg77.uhc.cmds.MsCommand;
import com.leontg77.uhc.cmds.MsgCommand;
import com.leontg77.uhc.cmds.MuteCommand;
import com.leontg77.uhc.cmds.NearCommand;
import com.leontg77.uhc.cmds.PmCommand;
import com.leontg77.uhc.cmds.PregenCommand;
import com.leontg77.uhc.cmds.PvPCommand;
import com.leontg77.uhc.cmds.RandomCommand;
import com.leontg77.uhc.cmds.RankCommand;
import com.leontg77.uhc.cmds.ReplyCommand;
import com.leontg77.uhc.cmds.ScenarioCommand;
import com.leontg77.uhc.cmds.SethealthCommand;
import com.leontg77.uhc.cmds.SetmaxhealthCommand;
import com.leontg77.uhc.cmds.SetspawnCommand;
import com.leontg77.uhc.cmds.SkullCommand;
import com.leontg77.uhc.cmds.SpecChatCommand;
import com.leontg77.uhc.cmds.SpectateCommand;
import com.leontg77.uhc.cmds.SpeedCommand;
import com.leontg77.uhc.cmds.SpreadCommand;
import com.leontg77.uhc.cmds.StaffChatCommand;
import com.leontg77.uhc.cmds.StartCommand;
import com.leontg77.uhc.cmds.StatsCommand;
import com.leontg77.uhc.cmds.TeamCommand;
import com.leontg77.uhc.cmds.TempbanCommand;
import com.leontg77.uhc.cmds.TextCommand;
import com.leontg77.uhc.cmds.TimeLeftCommand;
import com.leontg77.uhc.cmds.TimerCommand;
import com.leontg77.uhc.cmds.TlCommand;
import com.leontg77.uhc.cmds.TopCommand;
import com.leontg77.uhc.cmds.TpCommand;
import com.leontg77.uhc.cmds.TpsCommand;
import com.leontg77.uhc.cmds.UHCCommand;
import com.leontg77.uhc.cmds.UnbanCommand;
import com.leontg77.uhc.cmds.UnbanIPCommand;
import com.leontg77.uhc.cmds.VoteCommand;
import com.leontg77.uhc.cmds.WhitelistCommand;
import com.leontg77.uhc.cmds.WorldCommand;
import com.leontg77.uhc.inventory.InvGUI;
import com.leontg77.uhc.inventory.listener.ConfigListener;
import com.leontg77.uhc.inventory.listener.HOFListener;
import com.leontg77.uhc.inventory.listener.InvseeListener;
import com.leontg77.uhc.inventory.listener.SelectorListener;
import com.leontg77.uhc.inventory.listener.SpectatorListener;
import com.leontg77.uhc.inventory.listener.StatsListener;
import com.leontg77.uhc.listeners.BlockListener;
import com.leontg77.uhc.listeners.BuildProtectListener;
import com.leontg77.uhc.listeners.EntityListener;
import com.leontg77.uhc.listeners.InventoryListener;
import com.leontg77.uhc.listeners.LoginListener;
import com.leontg77.uhc.listeners.LogoutListener;
import com.leontg77.uhc.listeners.PlayerListener;
import com.leontg77.uhc.listeners.PortalListener;
import com.leontg77.uhc.listeners.WorldListener;
import com.leontg77.uhc.managers.BoardManager;
import com.leontg77.uhc.managers.PermissionsManager;
import com.leontg77.uhc.managers.TeamManager;
import com.leontg77.uhc.scenario.Scenario;
import com.leontg77.uhc.scenario.ScenarioManager;
import com.leontg77.uhc.ubl.UBL;
import com.leontg77.uhc.ubl.UBLListener;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;
import com.leontg77.uhc.worlds.AntiStripmine;
import com.leontg77.uhc.worlds.BiomeSwap;
import com.leontg77.uhc.worlds.WorldManager;

/**
 * Main class of the UHC plugin.
 * <p>
 * This class contains methods for prefixes, adding recipes, enabling and disabling.
 * 
 * @author LeonTG77
 */
public class Main extends JavaPlugin {
	public static Main plugin;

	public static final String PREFIX = "�4�lUHC �8� �7";
	public static final String NO_PERM_MSG = PREFIX + "�cYou can't use that command.";
	
	public static HashMap<CommandSender, CommandSender> msg = new HashMap<CommandSender, CommandSender>();
	public static HashMap<Player, int[]> rainbow = new HashMap<Player, int[]>();
	
	public static HashMap<String, Integer> teamKills = new HashMap<String, Integer>();
	public static HashMap<String, Integer> kills = new HashMap<String, Integer>();

	public static Recipe melonRecipe;
	public static Recipe headRecipe;
	
	
	@Override
	public void onDisable() {
		PluginDescriptionFile file = getDescription();
		getLogger().info(file.getName() + " is now disabled.");
		
		BiomeSwap.getInstance().resetBiomes();
		saveData();
		
		plugin = null;
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile file = getDescription();
		getLogger().info(file.getName() + " v" + file.getVersion() + " is now enabled.");
		getLogger().info("The plugin was created by LeonTG77.");
		
		plugin = this;
		Settings.getInstance().setup();
	    
		WorldManager.getInstance().loadWorlds();

		AntiStripmine.getInstance().setup();
		Announcer.getInstance().setup();
		Arena.getInstance().setup();
		
		BiomeSwap.getInstance().setup();
		Parkour.getInstance().setup();
		
		TeamManager.getInstance().setup();
		UBL.getInstance().reload();
		
		ScenarioManager.getInstance().setup();
		BoardManager.getInstance().setup();
		Game game = Game.getInstance();
		
		recoverData();
		addRecipes();
		
		if (game.hardcoreHearts()) {
			HardcoreHearts.enable();
		}

		PluginManager manager = Bukkit.getServer().getPluginManager();

		// register the leak checker.
		/*manager.registerEvents(new PlayerMemoryLeakChecker(this, new Function<String, Void>() {
            public Void apply(String name) {
            	String message = Main.PREFIX + "�4MEMORY LEAK: �7" + name + " was not garbage collected! �c(Logged out 30 seconds ago)";
            	
            	PlayerUtils.broadcast(message, "uhc.staff");
                
                Player player = Bukkit.getPlayer(name);
                
                if (player != null) {
                	player.sendMessage(Main.PREFIX + "Due to a rare and unexpected bug with you relogging earlier, your earlier player connection was not properly disconnected. " +
                	"You may be experiencing increased ping, lag spikes and unexpected glitches. You are advised to relog and wait at least 30 seconds to rejoin. Online staff has been informed.");
                	
                	player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1, 1);
                }
                return null;
            }
        }), this);*/
		
		// register all listeners.
		manager.registerEvents(new BlockListener(), this);
		manager.registerEvents(new BuildProtectListener(), this);
		manager.registerEvents(new EntityListener(), this);
		manager.registerEvents(new InventoryListener(), this);
		manager.registerEvents(new LoginListener(), this);
		manager.registerEvents(new LogoutListener(), this);
		manager.registerEvents(new PlayerListener(), this);
		manager.registerEvents(new PortalListener(), this);
		manager.registerEvents(new WorldListener(), this);
		manager.registerEvents(new UBLListener(), this);

		// register all inventory listeners.
		manager.registerEvents(new ConfigListener(), this);
		manager.registerEvents(new HOFListener(), this);
		manager.registerEvents(InvGUI.getGameInfo(), this);
		manager.registerEvents(new InvseeListener(), this);
		manager.registerEvents(new SelectorListener(), this);
		manager.registerEvents(new SpectatorListener(), this);
		manager.registerEvents(new StatsListener(), this);

		// register all commands.
		getCommand("arena").setExecutor(new ArenaCommand());
		getCommand("ban").setExecutor(new BanCommand());
		getCommand("banip").setExecutor(new BanIPCommand());
		getCommand("board").setExecutor(new BoardCommand());
		getCommand("border").setExecutor(new BorderCommand());
		getCommand("broadcast").setExecutor(new BroadcastCommand());
		getCommand("butcher").setExecutor(new ButcherCommand());
		getCommand("chat").setExecutor(new ChatCommand());
		getCommand("clearinv").setExecutor(new ClearInvCommand());
		getCommand("clearxp").setExecutor(new ClearXpCommand());
		getCommand("config").setExecutor(new ConfigCommand());
		getCommand("edit").setExecutor(new EditCommand());
		getCommand("end").setExecutor(new EndCommand());
		getCommand("feed").setExecutor(new FeedCommand());
		getCommand("fire").setExecutor(new FireCommand());
		getCommand("fly").setExecutor(new FlyCommand());
		getCommand("gamemode").setExecutor(new GamemodeCommand());
		getCommand("giveall").setExecutor(new GiveallCommand());
		getCommand("give").setExecutor(new GiveCommand());
		getCommand("heal").setExecutor(new HealCommand());
		getCommand("health").setExecutor(new HealthCommand());
		getCommand("helpop").setExecutor(new HelpopCommand());
		getCommand("hof").setExecutor(new HOFCommand());
		getCommand("hotbar").setExecutor(new HotbarCommand());
		getCommand("info").setExecutor(new InfoCommand());
		getCommand("invsee").setExecutor(new InvseeCommand());
		getCommand("kick").setExecutor(new KickCommand());
		getCommand("list").setExecutor(new ListCommand());
		getCommand("matchpost").setExecutor(new MatchpostCommand());
		getCommand("ms").setExecutor(new MsCommand());
		getCommand("msg").setExecutor(new MsgCommand());
		getCommand("mute").setExecutor(new MuteCommand());
		getCommand("near").setExecutor(new NearCommand());
		getCommand("pm").setExecutor(new PmCommand());
		getCommand("pregen").setExecutor(new PregenCommand());
		getCommand("pvp").setExecutor(new PvPCommand());
		getCommand("random").setExecutor(new RandomCommand());
		getCommand("rank").setExecutor(new RankCommand());
		getCommand("reply").setExecutor(new ReplyCommand());
		getCommand("scenario").setExecutor(new ScenarioCommand());
		getCommand("sethealth").setExecutor(new SethealthCommand());
		getCommand("setmaxhealth").setExecutor(new SetmaxhealthCommand());
		getCommand("skull").setExecutor(new SkullCommand());
		getCommand("sc").setExecutor(new SpecChatCommand());
		getCommand("spectate").setExecutor(new SpectateCommand());
		getCommand("setspawn").setExecutor(new SetspawnCommand());
		getCommand("speed").setExecutor(new SpeedCommand());
		getCommand("spread").setExecutor(new SpreadCommand());
		getCommand("ac").setExecutor(new StaffChatCommand());
		getCommand("start").setExecutor(new StartCommand());
		getCommand("stats").setExecutor(new StatsCommand());
		getCommand("team").setExecutor(new TeamCommand());
		getCommand("tempban").setExecutor(new TempbanCommand());
		getCommand("text").setExecutor(new TextCommand());
		getCommand("timeleft").setExecutor(new TimeLeftCommand());
		getCommand("timer").setExecutor(new TimerCommand());
		getCommand("teamloc").setExecutor(new TlCommand());
		getCommand("top").setExecutor(new TopCommand());
		getCommand("tp").setExecutor(new TpCommand());
		getCommand("tps").setExecutor(new TpsCommand());
		getCommand("uhc").setExecutor(new UHCCommand());
		getCommand("unban").setExecutor(new UnbanCommand());
		getCommand("unbanip").setExecutor(new UnbanIPCommand());
		getCommand("vote").setExecutor(new VoteCommand());
		getCommand("whitelist").setExecutor(new WhitelistCommand());
		getCommand("world").setExecutor(new WorldCommand());
		
		if (State.isState(State.NOT_RUNNING)) {
			File folder = new File(plugin.getDataFolder() + File.separator + "users" + File.separator);
			File playerData = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "playerdata");
			File stats = new File(Bukkit.getWorlds().get(0).getWorldFolder(), "stats");
			
			Bukkit.getServer().setIdleTimeout(60);
		
			int totalDatafiles = 0;
			int totalStatsfiles = 0;
			
			if (playerData.exists()) {
				for (File dataFiles : playerData.listFiles()) {
					try {
						dataFiles.delete();
						totalDatafiles++;
					} catch (Exception e) {
						getLogger().warning("Could not delete " + dataFiles.getName() + ".");
					}
				}
			}
			
			if (stats.exists()) {
				for (File statsFiles : stats.listFiles()) {
					try {
						statsFiles.delete();
						totalStatsfiles++;
					} catch (Exception e) {
						getLogger().warning("Could not delete " + statsFiles.getName() + ".");
					}
				}
			}

			plugin.getLogger().info("Deleted " + totalDatafiles + " player data files.");
			plugin.getLogger().info("Deleted " + totalStatsfiles + " player stats files.");
			
			for (File userFile : folder.listFiles()) {
				FileConfiguration config = YamlConfiguration.loadConfiguration(userFile);

				config.set("stats.cks", 0);
				config.set("stats.arenacks", 0);
				
				try {
					config.save(userFile);
				} catch (Exception e) {}
			}
		}
		
		if (State.isState(State.INGAME)) {
			manager.registerEvents(new SpecInfo(), this);
		}
		
		for (Player online : PlayerUtils.getPlayers()) {	
			PermissionsManager.addPermissions(online);
		}
		
		InvGUI.getGameInfo().updateStaff();
		InvGUI.getGameInfo().update();
		
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				for (Player online : PlayerUtils.getPlayers()) {
					PlayerInventory inv = online.getInventory();
					
					ItemStack hemlet = inv.getHelmet();
					ItemStack chestplate = inv.getChestplate();
					ItemStack leggings = inv.getLeggings();
					ItemStack boots = inv.getBoots();
					
					if (hemlet != null && hemlet.getType() == Material.LEATHER_HELMET) {
						inv.setHelmet(rainbowArmor(online, hemlet));
					}
					
					if (chestplate != null && chestplate.getType() == Material.LEATHER_CHESTPLATE) {
						inv.setChestplate(rainbowArmor(online, chestplate));
					}
					
					if (leggings != null && leggings.getType() == Material.LEATHER_LEGGINGS) {
						inv.setLeggings(rainbowArmor(online, leggings));
					}
					
					if (boots != null && boots.getType() == Material.LEATHER_BOOTS) {
						inv.setBoots(rainbowArmor(online, boots));
					}
					
					Game game = Game.getInstance();
					
					if (game.tabShowsHealthColor()) {
						String percentColor = NumberUtils.makePercent(online.getHealth()).substring(0, 2);
					    
					    online.setPlayerListName(percentColor + online.getName());
					}

					Scoreboard sb = BoardManager.getInstance().board;
					int percent = Integer.parseInt(NumberUtils.makePercent(online.getHealth()).substring(2));
					
					Objective tabList = sb.getObjective("tabHealth");
					Objective bellowName = sb.getObjective("nameHealth");
					
					if (tabList != null) {
						Score score = tabList.getScore(online.getName());
						score.setScore(percent);
					}
					
					if (bellowName != null) {
						Score score = bellowName.getScore(online.getName());
						score.setScore(percent);
					}
					
					if (online.getWorld().getName().equals("lobby")) {
						online.setPlayerWeather(WeatherType.DOWNFALL);
					}
				}
				
				for (World world : Bukkit.getWorlds()) {
					if (world.getName().equals("lobby")) {
						if (world.getDifficulty() != Difficulty.PEACEFUL) {
							world.setDifficulty(Difficulty.PEACEFUL);
						}
						
						if (world.getTime() != 18000) {
							world.setTime(18000);
						}
						continue;
					}
					
					if (world.getName().equals("arena")) {
						if (world.getDifficulty() != Difficulty.HARD) {
							world.setDifficulty(Difficulty.HARD);
						}
						
						if (world.getTime() != 6000) {
							world.setTime(6000);
						}
						continue;
					}
					
					if (world.getDifficulty() != Difficulty.HARD) {
						world.setDifficulty(Difficulty.HARD);
					}
				}
				
				ItemStack timer = new ItemStack (Material.WATCH);
				ItemMeta timerMeta = timer.getItemMeta();
				timerMeta.setDisplayName("�8� �6Timers �8�");
				
				List<String> lore = new ArrayList<String>();
				lore.add(" ");
				
				if (Game.getInstance().isRecordedRound()) {
					lore.add("�8� �7Current Episode: �a" + Timers.meetup);
					lore.add("�8� �7Time to next episode: �a" + Timers.time + " mins");
				}
				else if (GameUtils.getTeamSize().startsWith("No") || GameUtils.getTeamSize().startsWith("Open")) {
					lore.add("�8� �7There are no matches running.");
				}
				else if (!State.isState(State.INGAME)) {
					lore.add("�8� �7The game has not started yet.");
				}
				else {
					lore.add("�8� �7Time since start: �a" + DateUtils.ticksToString(Timers.timeSeconds));
					lore.add(Timers.pvpSeconds <= 0 ? "�8� �aPvP is enabled." : "�8� �7PvP in: �a" + DateUtils.ticksToString(Timers.pvpSeconds));
					lore.add(Timers.meetupSeconds <= 0 ? "�8� �6Meetup is now!" : "�8� �7Meetup in: �a" + DateUtils.ticksToString(Timers.meetupSeconds));
				}
				
				lore.add(" ");
				timerMeta.setLore(lore);
				timer.setItemMeta(timerMeta);
				InvGUI.getGameInfo().get().setItem(25, timer);
				lore.clear();
			}
		}, 1, 1);
	}
	
	/**
	 * Gets the servers tps.
	 * 
	 * @return The servers tps.
	 */
	public static double getTps() {
		return MinecraftServer.getServer().recentTps[0];
	}
	
	/**
	 * Get the spawnpoint of the lobby.
	 * 
	 * @return The lobby spawnpoint.
	 */
	public static Location getSpawn() {
		Settings settings = Settings.getInstance();
		
		World w = Bukkit.getServer().getWorld(settings.getData().getString("spawn.world", "lobby"));
		double x = settings.getData().getDouble("spawn.x", 0.5);
		double y = settings.getData().getDouble("spawn.y", 33.0);
		double z = settings.getData().getDouble("spawn.z", 0.5);
		float yaw = (float) settings.getData().getDouble("spawn.yaw", 0);
		float pitch = (float) settings.getData().getDouble("spawn.pitch", 0);
		
		Location loc = new Location(w, x, y, z, yaw, pitch);
		return loc;
	}
	
	/**
	 * Adds the golden head recipe.
	 */
	public void addRecipes() {
        ItemStack head = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = head.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD  + "Golden Head");
        meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood."));
        head.setItemMeta(meta); 

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        
        ShapedRecipe goldenmelon = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON)).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', Material.MELON);
        ShapedRecipe goldenhead = new ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', skull.getData());
        
        getServer().addRecipe(goldenmelon);
        getServer().addRecipe(goldenhead);

        melonRecipe = goldenmelon;
        headRecipe = goldenhead;

        getLogger().info("Golden Melon recipe added.");
        getLogger().info("Golden Head recipe added.");

        ShapelessRecipe ironplate = new ShapelessRecipe(new ItemStack(Material.IRON_INGOT, 2)).addIngredient(Material.IRON_PLATE);
        ShapelessRecipe goldplate = new ShapelessRecipe(new ItemStack(Material.GOLD_INGOT, 2)).addIngredient(Material.GOLD_PLATE);
        
        getServer().addRecipe(ironplate);
        getServer().addRecipe(goldplate);
        
        getLogger().info("IronPlate craftback recipe added.");
        getLogger().info("GoldPlate craftback recipe added.");
	}
	
	/**
	 * Save all the data to the data file.
	 */
	public void saveData() {
		Settings settings = Settings.getInstance();
		settings.getData().set("state", State.getState().name());
		
		List<String> list = new ArrayList<String>();
		
		for (Scenario scen : ScenarioManager.getInstance().getEnabledScenarios()) {
			list.add(scen.getName());
		}
		
		settings.getData().set("scenarios", list);
		
		for (Entry<String, Integer> tkEntry : teamKills.entrySet()) {
			settings.getData().set("teams.kills." + tkEntry.getKey(), tkEntry.getValue());
		}
		
		for (Entry<String, Integer> kEntry : kills.entrySet()) {
			settings.getData().set("kills." + kEntry.getKey(), kEntry.getValue());
		}
		
		for (Entry<String, List<String>> entry : TeamCommand.savedTeams.entrySet()) {
			settings.getData().set("teams.data." + entry.getKey(), entry.getValue());
		}
		settings.saveData();
	}
	
	/**
	 * Recover all the data from the data files.
	 */
	public void recoverData() {
		Settings settings = Settings.getInstance();
		State.setState(State.valueOf(settings.getData().getString("state", State.NOT_RUNNING.name())));
		
		try {
			for (String name : settings.getData().getConfigurationSection("kills").getKeys(false)) {
				kills.put(name, settings.getData().getInt("kills." + name));
			}
		} catch (Exception e) {
			getLogger().warning("Could not recover kills data.");
		}
		
		try {
			for (String name : settings.getData().getConfigurationSection("teams.kills").getKeys(false)) {
				teamKills.put(name, settings.getData().getInt("teams.kills" + name));
			}
		} catch (Exception e) {
			getLogger().warning("Could not recover team kills data.");
		}
		
		try {
			if (settings.getData().getConfigurationSection("team") != null) {
				for (String name : settings.getData().getConfigurationSection("teams.data").getKeys(false)) {
					TeamCommand.savedTeams.put("teams.data." + name, settings.getData().getStringList("teams.data." + name));
				}
			}
		} catch (Exception e) {
			getLogger().warning("Could not recover team data.");
		}
		
		try {
			for (String scen : settings.getData().getStringList("scenarios")) {
				ScenarioManager.getInstance().getScenario(scen).setEnabled(true);
			}
		} catch (Exception e) {
			getLogger().warning("Could not recover scenario data.");
		}
	}
	
	/**
	 * Change the color of the given type to a rainbow color.
	 *  
	 * @param player the players armor.
	 * @param type the type.
	 * @return The new colored leather armor.
	 */
	public ItemStack rainbowArmor(Player player, ItemStack item) {
		if (!rainbow.containsKey(player)) {
			rainbow.put(player, new int[] { 0, 0, 255 });
		}
		
		int[] rain = rainbow.get(player);
			
		int blue = rain[0];
		int green = rain[1];
		int red = rain[2];		

		if (red == 255 && blue == 0) {
			green++;
		}
			
		if (green == 255 && blue == 0) {
			red--;
		}
		
		if (green == 255 && red == 0) {
			blue++;
		}
			
		if (blue == 255 && red == 0) {
			green--;
		}
			
		if (green == 0 && blue == 255) {
			red++;
		}
			
		if (green == 0 && red == 255) {
			blue--;
		}
			
		rainbow.put(player, new int[] { blue, green, red });

    	ItemStack armor = new ItemStack (item.getType(), item.getAmount(), item.getDurability());
		LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
		meta.setColor(Color.fromBGR(blue, green, red));
		meta.setDisplayName(item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null);
		meta.setLore(item.hasItemMeta() && item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<String>());
		
		for (Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
			meta.addEnchant(ench.getKey(), ench.getValue(), true);
		}
		
		armor.setItemMeta(meta);
		return armor;
	}
	
	/**
	 * Border Shrink enum module.
	 * <p>
	 * Contains all the possible events when the border should shrink.
	 * 
	 * @author LeonTG77
	 */
	public enum BorderShrink {
		NEVER(""), START("from "), PVP("at "), MEETUP("at ");
		
		private String preText;
		
		/**
		 * Constructor for BorderShrink.
		 * 
		 * @param preText The text that fits before the shink name.
		 */
		private BorderShrink(String preText) {
			this.preText = preText;
		}
		
		/**
		 * Get the border pre text.
		 * 
		 * @return Pre text.
		 */
		public String getPreText() {
			return preText;
		}
	}
	
	/**
	 * Hardcore hearts class.
	 * <p> 
	 * This class manages the hardcore hearts feature.
	 *
	 * @author ghowden
	 */
	public static class HardcoreHearts extends PacketAdapter {
		private static ProtocolManager protocol = ProtocolLibrary.getProtocolManager();
		private static HardcoreHearts heart = new HardcoreHearts(Main.plugin);

		/**
		 * Constructor for HardcoreHearts.
		 * 
		 * @param plugin The main class of the plugin.
		 */
		public HardcoreHearts(Plugin plugin) {
			super(plugin, ListenerPriority.NORMAL, Play.Server.LOGIN);
		}

	    @Override
	    public void onPacketSending(PacketEvent event) {
	        if (!event.getPacketType().equals(Play.Server.LOGIN)) {
	        	return;
	        }
	        
	        event.getPacket().getBooleans().write(0, true);
	    }
	    
	    public static void enable() {
		    protocol.addPacketListener(heart);
	    }
	    
	    public static void disable() {
		    protocol.removePacketListener(heart);
	    }
	}
}