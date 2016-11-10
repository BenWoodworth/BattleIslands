package co.kepler.battleislands.phases.equipment;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.ItemUtil;
import co.kepler.battleislands.players.PlayerUtil;


public class EquipmentInv {
	private static final HashMap<UUID, EquipmentInv> inventories = new HashMap<UUID, EquipmentInv>();
	
	public static void disposeInv(Player p) {
		UUID id = p.getUniqueId();
		if (!inventories.containsKey(id)) return;
		BattleIslands.getPlayerConfig().setEquipmentPreferences(id, inventories.get(id).selection);
		inventories.remove(id);
		PlayerUtil.clearInv(p);
	}
	
	public static EquipmentInv getEquipmentInv(Player p) {
		if (inventories.containsKey(p.getUniqueId()))
			return inventories.get(p.getUniqueId());
		EquipmentInv result = new EquipmentInv(p);
		inventories.put(p.getUniqueId(), result);
		return result;
	}
	
	private UUID id;
	private int[] selection;
	
	private EquipmentInv(Player p) {
		id = p.getUniqueId();
		PlayerUtil.clearInv(p);
		selection = BattleIslands.getPlayerConfig().getEquipmentPreferences(id);
	}
	
	public void click(Player p, int slot) {
		int item = slot % 9;
		int newSelection = slot / 9 - 1;
		int slotStart = item + 9;
		
		if (item >= ItemUtil.getNumberOfItems()) return;
		
		ItemStack[] guiItems = ItemUtil.getGuiItems(p).get(item);
		
		if (slot < 9 || slot > 35 || newSelection >= guiItems.length || selection[item] == newSelection) return;
		
		p.getInventory().setItem(slotStart + selection[item] * 9, guiItems[selection[item]]);
		p.getInventory().setItem(slot, addGlow(guiItems[newSelection]));
		p.playSound(p.getLocation(), Sound.CLICK, 1F, 1F);
		selection[item] = newSelection;
	}
	
	private ItemStack addGlow(ItemStack is) {
		is = is.clone();
		ItemMeta im = is.getItemMeta();
		im.addEnchant(Enchantment.ARROW_DAMAGE, 0, true);
		is.setItemMeta(im);
		return is;
	}
	
	public int[] getSelection() {
		return selection;
	}
	
	public void setupPlayerInv() {
		int curItem = 0;
		Player p = Bukkit.getPlayer(id);
		Inventory inv = p.getInventory();
		for (ItemStack[] is : ItemUtil.getGuiItems(p)) {
			if (selection[curItem] >= is.length) selection[curItem] = 0;
			int start = curItem + 9;
			for (int i = 0; i < is.length; i++) {
				ItemStack toAdd = is[i];
				if (i == selection[curItem]) {
					toAdd = addGlow(toAdd);
				}
				inv.setItem(start + i * 9, toAdd);
			}
			curItem++;
		}
	}
}
