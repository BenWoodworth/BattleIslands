package co.kepler.battleislands.phases.battle;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.players.BITeam;

public class BattleInvListener implements Listener {

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		Inventory i = e.getInventory();
		if (i.getHolder() instanceof BlockState) {
			BlockState bs = (BlockState)i.getHolder();
			if (bs == null) return;
			BITeam playerTeam = BattleIslands.getPlayerManager()
					.getTeam(e.getPlayer().getUniqueId());
			BITeam spawnTeam = BITeam.fromWool(PhaseBattle.getTopWoolBlock(
					bs.getWorld(), bs.getX(), bs.getY(), bs.getZ()));
			if (spawnTeam != playerTeam) e.setCancelled(true);
		}
	}
}
