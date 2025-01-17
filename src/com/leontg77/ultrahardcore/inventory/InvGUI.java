package com.leontg77.ultrahardcore.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.GameUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * The inventory managing class.
 * <p>
 * This class contains methods for opening the selector inventory, game info inventory, hall of fame inventory and player inventories.
 * 
 * @author LeonTG77
 */
public class InvGUI {
	private static InvGUI manager = new InvGUI();
	
	public HashMap<Player, HashMap<Integer, Inventory>> pagesForPlayer = new HashMap<Player, HashMap<Integer, Inventory>>();
	public HashMap<Player, Integer> currentPage = new HashMap<Player, Integer>();
	
	public static HashMap<Inventory, BukkitRunnable> invsee = new HashMap<Inventory, BukkitRunnable>();

	private static TopStats topStats = new TopStats();
	private static GameInfo gameInfo = new GameInfo();
	private static HallOfFame hof = new HallOfFame();
	private static Stats stats = new Stats();
	
	/**
	 * Gets the instance of this class
	 * 
	 * @return The instance.
	 */
	public static InvGUI getInstance() {
		return manager;
	}
	
	public static GameInfo getGameInfo() {
		return gameInfo;
	}
	
	protected static TopStats getTopStats() {
		return topStats;
	}
	
	protected static Stats getStats() {
		return stats;
	}
	
	protected static HallOfFame getHOF() {
		return hof;
	}
	
	public void setup() {
		PluginManager manager = Bukkit.getPluginManager();
		Settings settings = Settings.getInstance();
		
		manager.registerEvents(gameInfo, Main.plugin);
		manager.registerEvents(topStats, Main.plugin);
		manager.registerEvents(stats, Main.plugin);
		manager.registerEvents(hof, Main.plugin);
		
		gameInfo.updateStaff();
		gameInfo.update();
		
		topStats.update();
		
		for (String host : settings.getHOF().getKeys(false)) {
			getHOF().update(host);
		}
		
		new BukkitRunnable() {
			public void run() {
				gameInfo.updateTimer();
			}
		}.runTaskTimer(Main.plugin, 1, 1);
	}
	
	/**
	 * Opens an inventory of all the online players that is playing.
	 * 
	 * @param player the player opening for.
	 * @return The opened inventory.
	 */
	public Inventory openSelector(Player player) {
		List<Player> list = GameUtils.getGamePlayers();
		Inventory inv = null;
		
		int pages = ((list.size() / 28) + 1);
		
		pagesForPlayer.put(player, new HashMap<Integer, Inventory>());
		
		for (int current = 1; current <= pages; current++) {
			inv = Bukkit.createInventory(null, 54, "» §7Player Selector");
			
			for (int i = 0; i < 35; i++) {
				if (list.size() < 1) {
					continue;
				}
				
				if (noItem(i)) {
					continue;
				}
				
				Player target = list.remove(0);
				
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setDisplayName("§a" + target.getName());
				meta.setLore(Arrays.asList("§7Click to teleport."));
				meta.setOwner(target.getName());
				item.setItemMeta(meta);
				inv.setItem(i, item);
			}
			
			ItemStack nextpage = new ItemStack (Material.ARROW);
			ItemMeta pagemeta = nextpage.getItemMeta();
			pagemeta.setDisplayName(ChatColor.GREEN + "Next page");
			pagemeta.setLore(Arrays.asList("§7Switch to the next page."));
			nextpage.setItemMeta(pagemeta);
			
			ItemStack prevpage = new ItemStack (Material.ARROW);
			ItemMeta prevmeta = prevpage.getItemMeta();
			prevmeta.setDisplayName(ChatColor.GREEN + "Previous page");
			prevmeta.setLore(Arrays.asList("§7Switch to the previous page."));
			prevpage.setItemMeta(prevmeta);
			
			if (current != 1) {
				inv.setItem(47, prevpage);
			}
			
			if (current != pages) {
				inv.setItem(51, nextpage);
			}
			
			pagesForPlayer.get(player).put(current, inv);
		}
		
		inv = pagesForPlayer.get(player).get(1);
		currentPage.put(player, 1);
		
		player.openInventory(inv);
		return inv;
	}

	/**
	 * Opens the inventory of the given target for the given player.
	 * 
	 * @param player player to open for.
	 * @param target the players inv to use.
	 * 
	 * @return The opened inventory.
	 */
	public Inventory openPlayerInventory(final Player player, final Player target) {
		final Inventory inv = Bukkit.getServer().createInventory(target, 54, "» §7" + target.getName() + "'s Inventory");
	
		invsee.put(inv, new BukkitRunnable() {
			public void run() {
				inv.setItem(0, target.getInventory().getHelmet());
				inv.setItem(1, target.getInventory().getChestplate());
				inv.setItem(2, target.getInventory().getLeggings());
				inv.setItem(3, target.getInventory().getBoots());
				inv.setItem(5, target.getItemInHand());
				inv.setItem(6, target.getItemOnCursor());
				
				ItemStack info = new ItemStack (Material.BOOK);
				ItemMeta infoMeta = info.getItemMeta();
				infoMeta.setDisplayName("§8» §6Player Info §8«");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("§8» §7Name: §a" + target.getName());
				lore.add(" ");
				int health = (int) target.getHealth();
				lore.add("§8» §7Hearts: §6" + (((double) health) / 2) + "§4♥");
				lore.add("§8» §7Percent: §6" + NumberUtils.makePercent(target.getHealth()) + "%");
				lore.add("§8» §7Hunger: §6" + (((double) target.getFoodLevel()) / 2));
				lore.add("§8» §7XP level: §6" + target.getLevel());
				lore.add("§8» §7Location: §6x:" + target.getLocation().getBlockX() + ", y:" + target.getLocation().getBlockY() + ", z:" + target.getLocation().getBlockZ() + " (" + target.getWorld().getEnvironment().name().replaceAll("_", "").toLowerCase().replaceAll("normal", "overworld") + ")");
				lore.add(" ");
				lore.add("§8» §cPotion effects:");
				
				if (target.getActivePotionEffects().size() == 0) {
					lore.add("§8» §7None");
				}
				
				for (PotionEffect effects : target.getActivePotionEffects()) {
					lore.add("§8» §7P:§6" + NameUtils.getPotionName(effects.getType()) + " §7T:§6" + (effects.getAmplifier() + 1) + " §7D:§6" + DateUtils.ticksToString(effects.getDuration() / 20));
				}
				
				infoMeta.setLore(lore);
				info.setItemMeta(infoMeta);
				inv.setItem(8, info);
				lore.clear();
				
				for (int i = 9; i < 18; i++) {
					ItemStack glass = new ItemStack (Material.STAINED_GLASS_PANE, 1, (short) 15);
					ItemMeta glassMeta = glass.getItemMeta();
					glassMeta.setDisplayName("§0:>");
					glass.setItemMeta(glassMeta);
					inv.setItem(8, info);
					inv.setItem(i, glass);
				}
				
				int i = 18;
				
				for (ItemStack item : target.getInventory().getContents()) {
					inv.setItem(i, item);
					i++;
				}
				
				player.updateInventory();
			}
		});
		
		invsee.get(inv).runTaskTimer(Main.plugin, 1, 1);
		player.openInventory(inv);
		
		return inv;
	}
	
	/**
	 * Opens an inventory of the given hosts hall of fame.
	 * 
	 * @param player the player opening for.
	 * @param host The owner of the hall of fame.
	 * @return The opened inventory.
	 */
	public Inventory openHOF(Player player, String host) {
		HallOfFame hof = getHOF();
		Inventory inv = hof.get(host);
		
		hof.currentHost.put(player.getName(), host);
		hof.currentPage.put(player.getName(), 1);
		
		player.openInventory(inv);
		return inv;
	}
	
	/**
	 * Opens stats inventory of the given user.
	 * 
	 * @param target The owner of the stats.
	 * @return The opened inventory.
	 */
	public Inventory openStats(Player player, User target) {
		Inventory inv = stats.get(target);
		
		player.openInventory(inv);
		return inv;
	}
	
	/**
	 * Opens top tats inventory.
	 * 
	 * @return The opened inventory.
	 */
	public Inventory openTopStats(Player player) {
		Inventory inv = topStats.get();
		
		player.openInventory(inv);
		return inv;
	}
	
	/**
	 * Open the config option inventory for the given player.
	 * 
	 * @param player The player opening for.
	 * @return The opened inventory.
	 */
	public Inventory openConfigOptions(Player player) {
		Inventory inv = Bukkit.getServer().createInventory(null, 54, "» §7Game config");
		Game game = Game.getInstance();
		
		ItemStack absorption = new ItemStack (Material.GOLDEN_APPLE);
		ItemMeta absorptionMeta = absorption.getItemMeta();
		absorptionMeta.setDisplayName((game.absorption() ? "§a" : "§c") + "Absorption");
		absorption.setItemMeta(absorptionMeta);
		inv.setItem(0, absorption);
		
		ItemStack heads = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta headsMeta = heads.getItemMeta();
		headsMeta.setDisplayName((game.goldenHeads() ? "§a" : "§c") + "Golden Heads");
		heads.setItemMeta(headsMeta);
		inv.setItem(1, heads);
		
		ItemStack notchApples = new ItemStack (Material.GOLDEN_APPLE, 1, (short) 1);
		ItemMeta notchMeta = notchApples.getItemMeta();
		notchMeta.setDisplayName((game.notchApples() ? "§a" : "§c") + "Notch Apples");
		notchApples.setItemMeta(notchMeta);
		inv.setItem(3, notchApples);
		
		ItemStack hearts = new ItemStack (Material.INK_SACK, 1, (short) 1);
		ItemMeta heartsMeta = hearts.getItemMeta();
		heartsMeta.setDisplayName((game.heartsOnTab() ? "§a" : "§c") + "Hearts on tab");
		hearts.setItemMeta(heartsMeta);
		inv.setItem(5, hearts);
		
		ItemStack hardcore = new ItemStack (Material.REDSTONE);
		ItemMeta hardcoreMeta = hardcore.getItemMeta();
		hardcoreMeta.setDisplayName((game.hardcoreHearts() ? "§a" : "§c") + "Hardcore Hearts");
		hardcore.setItemMeta(hardcoreMeta);
		inv.setItem(6, hardcore);
		
		ItemStack tab = new ItemStack (Material.SIGN);
		ItemMeta tabMeta = tab.getItemMeta();
		tabMeta.setDisplayName((game.tabShowsHealthColor() ? "§a" : "§c") + "Tab health color");
		tab.setItemMeta(tabMeta);
		inv.setItem(7, tab);
		
		ItemStack rr = new ItemStack (Material.PAINTING);
		ItemMeta rrMeta = rr.getItemMeta();
		rrMeta.setDisplayName((game.isRecordedRound() ? "§a" : "§c") + "Recorded Round");
		rr.setItemMeta(rrMeta);
		inv.setItem(8, rr);
		
		ItemStack nether = new ItemStack (Material.NETHER_STALK);
		ItemMeta netherMeta = nether.getItemMeta();
		netherMeta.setDisplayName((game.nether() ? "§a" : "§c") + "Nether");
		nether.setItemMeta(netherMeta);
		inv.setItem(18, nether);
		
		ItemStack end = new ItemStack (Material.ENDER_PORTAL_FRAME);
		ItemMeta endMeta = end.getItemMeta();
		endMeta.setDisplayName((game.theEnd() ? "§a" : "§c") + "The End");
		end.setItemMeta(endMeta);
		inv.setItem(19, end);
		
		ItemStack strip = new ItemStack (Material.DIAMOND_PICKAXE);
		ItemMeta stripMeta = strip.getItemMeta();
		stripMeta.setDisplayName((game.antiStripmine() ? "§a" : "§c") + "Anti Stripmine");
		stripMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		strip.setItemMeta(stripMeta);
		inv.setItem(21, strip);
		
		ItemStack death = new ItemStack (Material.BLAZE_ROD);
		ItemMeta deathMeta = death.getItemMeta();
		deathMeta.setDisplayName((game.deathLightning() ? "§a" : "§c") + "Death Lightning");
		death.setItemMeta(deathMeta);
		inv.setItem(22, death);
		
		ItemStack horse = new ItemStack (Material.SADDLE);
		ItemMeta horseMeta = horse.getItemMeta();
		horseMeta.setDisplayName((game.horses() ? "§a" : "§c") + "Horses");
		horse.setItemMeta(horseMeta);
		inv.setItem(24, horse);
		
		ItemStack armor = new ItemStack (Material.IRON_BARDING);
		ItemMeta armorMeta = armor.getItemMeta();
		armorMeta.setDisplayName((game.horseArmor() ? "§a" : "§c") + "Horse Armor");
		armor.setItemMeta(armorMeta);
		inv.setItem(25, armor);
		
		ItemStack healing = new ItemStack (Material.BREAD);
		ItemMeta healingMeta = healing.getItemMeta();
		healingMeta.setDisplayName((game.horseHealing() ? "§a" : "§c") + "Horse Healing");
		healing.setItemMeta(healingMeta);
		inv.setItem(26, healing);
		
		ItemStack ghast = new ItemStack (Material.GHAST_TEAR);
		ItemMeta ghastMeta = ghast.getItemMeta();
		ghastMeta.setDisplayName((game.ghastDropGold() ? "§a" : "§c") + "Ghast drop gold");
		ghast.setItemMeta(ghastMeta);
		inv.setItem(43, ghast);
		
		ItemStack melon = new ItemStack (Material.SPECKLED_MELON);
		ItemMeta melonMeta = melon.getItemMeta();
		melonMeta.setDisplayName((game.goldenMelonNeedsIngots() ? "§a" : "§c") + "Golden Melon needs ingots");
		melon.setItemMeta(melonMeta);
		inv.setItem(44, melon);
		
		ItemStack shears = new ItemStack (Material.SHEARS);
		ItemMeta shearsMeta = shears.getItemMeta();
		shearsMeta.setDisplayName((game.shears() ? "§a" : "§c") + "Shears");
		shears.setItemMeta(shearsMeta);
		inv.setItem(45, shears);
		
		ItemStack terrain = new ItemStack (Material.STONE, 1, (short) 1);
		ItemMeta terrainMeta = terrain.getItemMeta();
		terrainMeta.setDisplayName((game.newStone() ? "§a" : "§c") + "1.8 Stone");
		terrain.setItemMeta(terrainMeta);
		inv.setItem(46, terrain);
		
		ItemStack bookshelves = new ItemStack (Material.BOOKSHELF);
		ItemMeta bookMeta = bookshelves.getItemMeta();
		bookMeta.setDisplayName((game.bookshelves() ? "§a" : "§c") + "Bookshelves");
		bookshelves.setItemMeta(bookMeta);
		inv.setItem(47, bookshelves);
		
		ItemStack tier2 = new ItemStack (Material.GLOWSTONE_DUST);
		ItemMeta tier2Meta = tier2.getItemMeta();
		tier2Meta.setDisplayName((game.tier2() ? "§a" : "§c") + "Tier 2");
		tier2.setItemMeta(tier2Meta);
		inv.setItem(50, tier2);
		
		ItemStack splash = new ItemStack (Material.POTION, 1, (short) 16424);
		ItemMeta splashMeta = splash.getItemMeta();
		splashMeta.setDisplayName((game.splash() ? "§a" : "§c") + "Splash");
		splash.setItemMeta(splashMeta);
		inv.setItem(51, splash);
		
		ItemStack str = new ItemStack (Material.BLAZE_POWDER);
		ItemMeta strMeta = str.getItemMeta();
		strMeta.setDisplayName((game.strength() ? "§a" : "§c") + "Strength");
		str.setItemMeta(strMeta);
		inv.setItem(52, str);
		
		ItemStack nerf = new ItemStack (Material.POTION, 1, (short) 8233);
		ItemMeta nerfMeta = nerf.getItemMeta();
		nerfMeta.setDisplayName((game.nerfedStrength() ? "§a" : "§c") + "Nerfed Strength");
		nerf.setItemMeta(nerfMeta);
		inv.setItem(53, nerf);
		
		player.openInventory(inv);
		
		return inv;
	}
	
	public void openGameInfo(Player player) {
		openGameInfo(ImmutableList.of(player));
	}
	
	/**
	 * Opens the game information inventory.
	 * 
	 * @param players player to open for.
	 */
	public void openGameInfo(List<Player> players) {
		Inventory inv = gameInfo.get();
		
		for (Player player : players) {
			player.openInventory(inv);
		}
	}
	
	/**
	 * Check if this slot shall have no items.
	 * 
	 * @param slot The slot.
	 * @return True if it shouldn't, false otherwise.
	 */
	protected boolean noItem(int slot) {
		switch (slot) {
		case 0:
		case 8:
		case 9:
		case 17:
		case 18:
		case 26:
		case 27:
		case 35:
		case 36:
			return true;
		default:
			return false;
		}
	}
}