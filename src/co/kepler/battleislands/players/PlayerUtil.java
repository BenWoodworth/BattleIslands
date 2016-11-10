package co.kepler.battleislands.players;

import org.bukkit.entity.Player;

public class PlayerUtil {

	public static void clearInv(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
	}
}
