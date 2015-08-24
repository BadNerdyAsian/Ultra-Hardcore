package com.leontg77.uhc;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.uhc.Main.Border;
import com.leontg77.uhc.utils.DateUtils;
import com.leontg77.uhc.utils.GameUtils;
import com.leontg77.uhc.utils.NameUtils;
import com.leontg77.uhc.utils.NumberUtils;
import com.leontg77.uhc.utils.PlayerUtils;

/**
 * The inventory managing class.
 * <p>
 * This class contains methods for opening the selector inventory, rules inventory and player inventories.
 * 
 * @author LeonTG77
 */
public class InvGUI {
	private static InvGUI manager = new InvGUI();
	
	/**
	 * Gets the instance of this class
	 * 
	 * @return The instance.
	 */
	public static InvGUI getManager() {
		return manager;
	}
	
	/**
	 * Opens an inventory of all the online players that is playing.
	 * 
	 * @param player the player opening for.
	 * @return The opened inventory.
	 */
	public Inventory openSelector(Player player) {
		Inventory inv = Bukkit.createInventory(null, PlayerUtils.playerInvSize(), "Player Selector");
	
		for (Player online : PlayerUtils.getPlayers()) {
			if (!Spectator.getManager().isSpectating(online)) {
				ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setDisplayName(ChatColor.GOLD + online.getName());
				meta.setOwner(online.getName());
				meta.setLore(Arrays.asList(ChatColor.GRAY + "Click to teleport to §a" + online.getName() + "§f."));
				item.setItemMeta(meta);
				inv.addItem(item);
			}	
		}
		
		player.openInventory(inv);
		return inv;
	}
	
	/**
	 * Opens the inventory of a target player.
	 * 
	 * @param player player to open for.
	 * @param target the players inv to use.
	 * @return The opened inventory.
	 */
	public Inventory openInv(Player player, final Player target) {
		final Inventory inv = Bukkit.getServer().createInventory(target, 54, "Player Inventory");
	
		Main.invsee.put(inv, new BukkitRunnable() {
			public void run() {
				if (inv.getItem(0) != target.getInventory().getHelmet()) {
					inv.setItem(0, target.getInventory().getHelmet());
				}

				if (inv.getItem(1) != target.getInventory().getChestplate()) {
					inv.setItem(1, target.getInventory().getChestplate());
				}

				if (inv.getItem(2) != target.getInventory().getLeggings()) {
					inv.setItem(2, target.getInventory().getLeggings());
				}

				if (inv.getItem(3) != target.getInventory().getBoots()) {
					inv.setItem(3, target.getInventory().getBoots());
				}

				if (inv.getItem(5) != target.getItemInHand()) {
					inv.setItem(5, target.getItemInHand());
				}

				if (inv.getItem(6) != target.getItemOnCursor()) {
					inv.setItem(6, target.getItemOnCursor());
				}
				
				ItemStack info = new ItemStack (Material.BOOK);
				ItemMeta infoMeta = info.getItemMeta();
				infoMeta.setDisplayName("§4Player information");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("§aName: §7" + target.getName());
				lore.add(" ");
				int health = (int) target.getHealth();
				lore.add("§aHearts: §7" + (((double) health) / 2) + "§4♥");
				lore.add("§a% Health: §7" + NumberUtils.makePercent(target.getHealth()) + "%");
				lore.add("§aHunger: §7" + (target.getFoodLevel() / 2));
				lore.add("§aXp level: §7" + target.getLevel());
				lore.add("§aLocation: §7x:" + target.getLocation().getBlockX() + ", y:" + target.getLocation().getBlockY() + ", z:" + target.getLocation().getBlockZ() + " (" + target.getWorld().getEnvironment().name().replaceAll("_", "").toLowerCase().replaceAll("normal", "overworld") + ")");
				lore.add(" ");
				lore.add("§cPotion effects:");
				if (target.getActivePotionEffects().size() == 0) {
					lore.add(ChatColor.GRAY + "None");
				}
				for (PotionEffect effects : target.getActivePotionEffects()) {
					if ((effects.getDuration() / 20) > 0) {
						lore.add("§aP:§7" + NameUtils.getPotionName(effects.getType()) + " §aT:§7" + (effects.getAmplifier() + 1) + " §aD:§7" + DateUtils.ticksToString(effects.getDuration() / 20));
					}
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
					
					if (inv.getItem(i) != glass) {
						inv.setItem(i, glass);
					}
				}
				
				int i = 18;
				for (ItemStack item : target.getInventory().getContents()) {
					if (inv.getItem(i) != item) {
						inv.setItem(i, item);
					}
					i++;
				}
			}
		});
		Main.invsee.get(inv).runTaskTimer(Main.plugin, 1, 1);
		
		player.openInventory(inv);
		return inv;
	}

	/**
	 * Open the rules inventory for the given player
	 * 
	 * @param player the player
	 * @return The opened inventory.
	 */
	public Inventory openRules(Player player) {
		Inventory inv = Bukkit.getServer().createInventory(null, 9, "Arctic UHC Rules");
		
		ItemStack general = new ItemStack (Material.SIGN);
		ItemMeta generalMeta = general.getItemMeta();
		generalMeta.setDisplayName("§6General Rules");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(" ");
		lore.add("§aStarter food: §75 minutes of starter saturation.");
		lore.add("§aSpoiling: §7Not allowed after you died.");
		lore.add("§aSpamming: §7Mute, ban if excessive.");
		lore.add("§aSwearing: §7Allowed unless it's done excessively.");
		lore.add("§aTowering: §7Allowed, but come down at meetup.");
		lore.add("§aForting: §7Allowed before meetup.");
		lore.add("§aHorses: §7Enabled.");
		lore.add("§aHorse Healing: §7Enabled.");
		lore.add("§aHorse Armor: §7Enabled.");
		lore.add(" ");
		lore.add("§aAbsorption: §7" + (Main.absorption ? "Enabled." : "Disabled."));
		lore.add("§aGolden Heads: §7" + (Main.goldenheads ? "Enabled, they heal " + (Settings.getInstance().getConfig().getInt("feature.goldenheads.heal") / 2) + " hearts." : "Disabled."));
		lore.add("§aPearl Damage: §7" + (Main.pearldamage ? "Enabled." : "Disabled."));
		lore.add("§aNotch Apples: §7" + (Main.notchapples ? "Enabled." : "Disabled."));
		lore.add("§aDeath Lightning: §7" + (Main.deathlightning ? "Enabled." : "Disabled."));
		lore.add("§aBorder shrinks: §7" + ((Main.border == Border.NEVER ? "Never" : (Main.border == Border.START ? "From " : "At ") + Main.border.name().toLowerCase())) + ".");
		lore.add(" ");
		generalMeta.setLore(lore);
		general.setItemMeta(generalMeta);
		inv.setItem(0, general);

		ItemStack mining = new ItemStack (Material.DIAMOND_PICKAXE);
		ItemMeta miningMeta = mining.getItemMeta();
		miningMeta.setDisplayName("§6Mining Rules");
		ArrayList<String> lore2 = new ArrayList<String>();
		lore2.add(" ");
		lore2.add("§aStripmining: §7Allowed.");
		lore2.add("§aBranchmining: §7Allowed.");
		lore2.add("§aPokeholing: §7Allowed.");
		lore2.add("§aBlastmining: §7Allowed.");
		lore2.add("§aStaircasing: §7Allowed.");
		lore2.add("§aRollercoastering: §7Allowed.");
		lore2.add("§aDigging to sounds: §7Allowed.");
		lore2.add("§aDigging to entities: §7Allowed.");
		lore2.add("§aDigging to players: §7Only if you see them.");
		lore2.add(" ");
		miningMeta.setLore(lore2);
		mining.setItemMeta(miningMeta);
		inv.setItem(2, mining);
		
		ItemStack pvp = new ItemStack (Material.IRON_SWORD);
		ItemMeta pvpMeta = pvp.getItemMeta();
		pvpMeta.setDisplayName("§6PvP Rules");
		ArrayList<String> lore3 = new ArrayList<String>();
		lore3.add(" ");
		lore3.add("§aiPvP: §7Not allowed before pvp.");
		lore3.add("§aTeam Killing: §7" + (GameUtils.getTeamSize().startsWith("r") ? "Not allowed." : "Allowed."));
		lore3.add("§aStalking: §7Allowed.");
		lore3.add("§aStealing: §7Allowed.");
		lore3.add("§aCrossteaming: §7Not allowed, you will get banned.");
		lore3.add(" ");
		pvpMeta.setLore(lore3);
		pvp.setItemMeta(pvpMeta);
		inv.setItem(4, pvp);
		
		ItemStack nether = new ItemStack (Material.LAVA_BUCKET);
		ItemMeta netherMeta = nether.getItemMeta();
		netherMeta.setDisplayName("§6Nether Rules");
		ArrayList<String> lore4 = new ArrayList<String>();
		lore4.add(" ");
		lore4.add("§aTrapping: §7Not Allowed.");
		lore4.add("§aCamping: §7Not Allowed.");
		lore4.add("§aStrength: §7" + (Main.nerfedStrength ? "Both tiers nerfed." : "Vanilla."));
		lore4.add("§aTier 2: §7On for all potions.");
		lore4.add("§aSplash: §7On for all potions.");
		lore4.add("§aGhast Drop: §7" + (Main.ghastdrops ? "Gold ingot." : "Ghast tear."));
		lore4.add(" ");
		lore4.add("§aNether: §7" + (Main.nether ? "Enabled." : "Disabled."));
		lore4.add("§aThe End: §7" + (Main.theend ? "Enabled." : "Disabled."));
		lore4.add(" ");
		netherMeta.setLore(lore4);
		nether.setItemMeta(netherMeta);
		inv.setItem(6, nether);
		
		ItemStack rates = new ItemStack (Material.APPLE);
		ItemMeta ratesMeta = rates.getItemMeta();
		ratesMeta.setDisplayName("§6Rates");
		ArrayList<String> lore5 = new ArrayList<String>();
		lore5.add(" ");
		lore5.add("§aApple rates: §70.5%, " + (Main.shears ? "Shears work" : "Shears does not work") + ".");
		lore5.add("§aFlintrates: §7" + Main.flintrate + "%.");
		lore5.add("§aMob rates: §7Vanilla.");
		lore5.add("§aOre rates: §7Vanilla.");
		lore5.add("§aCave rates: §7Vanilla.");
		lore5.add("§aWitch Potion: §7Increased.");
		lore5.add(" ");
		ratesMeta.setLore(lore5);
		rates.setItemMeta(ratesMeta);
		inv.setItem(8, rates);
		
		player.openInventory(inv);
		return inv;
	}
}