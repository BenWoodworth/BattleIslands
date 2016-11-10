package co.kepler.battleislands.phases.equipment;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class EquipmentInvListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().equals(e.getWhoClicked().getInventory())) return;
		e.setResult(Result.DENY);
		if (e.getClick() == ClickType.LEFT) {
			Player p = (Player)e.getWhoClicked();
			EquipmentInv.getEquipmentInv(p).click(p, e.getSlot());
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		if (e.getInventory().getType() == InventoryType.CRAFTING)
			e.setCancelled(true);
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		e.setCancelled(true);
	}
}
