package co.kepler.battleislands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import co.kepler.battleislands.phases.equipment.EquipmentInv;
import co.kepler.battleislands.players.BITeam;

public class ItemUtil {
	private static final ItemFlag[] ITEM_FLAGS = new ItemFlag[] {
		ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON,
		ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS
	};

	private static HashMap<ItemStack, BattleItemInfo> battleItemInfo;
	private static HashMap<BITeam, List<ItemStack[]>> equipmentItems;
	private static HashMap<BITeam, List<ItemStack[]>> guiItems;
	private static HashMap<String, Material> materials;
	private static HashMap<String, String> dataVars;
	private static int numItems;

	@SuppressWarnings("deprecation")
	public static void init(FileConfiguration c) {
		equipmentItems = new HashMap<BITeam, List<ItemStack[]>>();
		guiItems = new HashMap<BITeam, List<ItemStack[]>>();

		ConfigurationSection itemSection = c.getConfigurationSection("items");
		ConfigurationSection equipmentSection = c.getConfigurationSection("equipment");
		ConfigurationSection dataVarSection = c.getConfigurationSection("dataVars");

		materials = new HashMap<String, Material>();
		for (String s : itemSection.getKeys(false)) {
			materials.put(s, Material.getMaterial(itemSection.getInt(s)));
		}

		dataVars = new HashMap<String, String>();
		for (String s : dataVarSection.getKeys(false)) {
			dataVars.put(s, dataVarSection.getString(s));
		}

		ConfigurationSection equipmentSubsection;
		ConfigurationSection equipmentItemSection;
		List<ItemStack[]> equipmentItemsList = new ArrayList<ItemStack[]>();
		List<ItemStack[]> guiItemsList = new ArrayList<ItemStack[]>();
		Set<String> equipSectionKeys = equipmentSection.getKeys(false);
		numItems = equipSectionKeys.size();
		for (String itemType : equipSectionKeys) {
			equipmentSubsection = equipmentSection.getConfigurationSection(itemType);
			Material material = materials.get(itemType);
			int index = 0;
			Set<String> equipSubsectionKeys = equipmentSubsection.getKeys(false);
			ItemStack[] equipmentItems = new ItemStack[equipSubsectionKeys.size()];
			ItemStack[] guiItems = new ItemStack[equipSubsectionKeys.size()];
			for (String itemKey : equipSubsectionKeys) {
				equipmentItemSection = equipmentSubsection.getConfigurationSection(itemKey);
				equipmentItems[index] = createGameItem(material, equipmentItemSection);
				guiItems[index] = createGuiItem(material, equipmentItemSection);
				index++;
			}
			equipmentItemsList.add(equipmentItems);
			guiItemsList.add(guiItems);
		}

		for (BITeam team : BITeam.getTeams()) {
			equipmentItems.put(team, colorItems(equipmentItemsList, team));
			guiItems.put(team, colorItems(guiItemsList, team));
		}

		battleItemInfo = new HashMap<ItemStack, BattleItemInfo>();
		ConfigurationSection battleItemSection = c.getConfigurationSection("battleItems");
		ConfigurationSection battleItemSubsection;
		for (String s : battleItemSection.getKeys(false)) {
			battleItemSubsection = battleItemSection.getConfigurationSection(s);
			Material m = materials.get(s);
			ItemStack is = new ItemStack(m, battleItemSubsection.getInt("amount", 1));
			is = setJsonData(is, battleItemSubsection.getString("data"));
			battleItemInfo.put(is, new BattleItemInfo(
					battleItemSubsection.getInt("seconds"),
					battleItemSubsection.getInt("amount", 1),
					battleItemSubsection.getInt("max", -1)));
		}
	}

	public static int getNumberOfItems() {
		return numItems;
	}

	private static ItemStack createGameItem(Material m, ConfigurationSection c) {
		ItemStack is = setJsonData(new ItemStack(m), c.getString("data"));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(c.getName());
		im.addItemFlags(ITEM_FLAGS);
		im.spigot().setUnbreakable(true);
		is.setItemMeta(im);
		return is;
	}

	private static ItemStack createGuiItem(Material m, ConfigurationSection c) {
		ItemStack is = new ItemStack(m);
		List<String> lore = new ArrayList<String>();
		lore.addAll(c.getStringList("description"));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("§7" + c.getName());
		im.setLore(lore);
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
		is.setItemMeta(im);
		return is;
	}

	private static List<ItemStack[]> colorItems(List<ItemStack[]> items, BITeam team) {
		List<ItemStack[]> result = new ArrayList<ItemStack[]>();
		ItemStack[] toAdd;
		for (ItemStack[] stacks : items) {
			toAdd = new ItemStack[stacks.length];
			for (int i = 0; i < stacks.length; i++) {
				toAdd[i] = colorItem(stacks[i], team);
			}
			result.add(toAdd);
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack colorItem(ItemStack is, BITeam team) {
		is = is.clone();
		switch(is.getType()) {
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
			LeatherArmorMeta lam = (LeatherArmorMeta)is.getItemMeta();
			lam.setColor(team.getLeatherColor());
			is.setItemMeta(lam);
			break;
		case WOOL:
		case STAINED_CLAY:
			is.setDurability(team.getDyeColor().getData());
			break;
		default:
		}
		return is;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack setJsonData(ItemStack is, String data) {
		if (data == null || data.length() == 0) return is;
		for (String key : dataVars.keySet()) {
			data = data.replace(key, dataVars.get(key));
		}
		return Bukkit.getUnsafe().modifyItemStack(is, data);
	}

	public static List<ItemStack[]> getEquipmentItems(Player p)
	{ return equipmentItems.get(BattleIslands.getPlayerManager().getTeam(p.getUniqueId())); }
	public static List<ItemStack[]> getGuiItems(Player p)
	{ return guiItems.get(BattleIslands.getPlayerManager().getTeam(p.getUniqueId())); }

	public static ItemStack[] getSelectedEquipment(Player p) {
		int[] selection = EquipmentInv.getEquipmentInv(p).getSelection();
		ItemStack[] result = new ItemStack[selection.length];
		List<ItemStack[]> equipment = getEquipmentItems(p);
		for (int i = 0; i < result.length; i++) {
			result[i] = equipment.get(i)[selection[i]];
		}
		return result;
	}

	public static void giveEquipment(Player p) {
		ItemStack[] armor = new ItemStack[4];
		for (ItemStack is : getSelectedEquipment(p)) {
			switch (is.getType()) {
			case LEATHER_HELMET:     armor[3] = is; break;
			case LEATHER_CHESTPLATE: armor[2] = is; break;
			case LEATHER_LEGGINGS:   armor[1] = is; break;
			case LEATHER_BOOTS:      armor[0] = is; break;
			default: p.getInventory().addItem(is);  break;
			}
		}
		p.getInventory().setArmorContents(armor);
	}

	
	public static HashMap<ItemStack, BattleItemInfo> getBattleItemTimings() {
		return battleItemInfo;
	}
	
	public static class BattleItemInfo {
		public final int seconds, amount, max;
		private BattleItemInfo(int s, int a, int m)
		{ seconds = s; amount = a; max = m; }
	}
}
