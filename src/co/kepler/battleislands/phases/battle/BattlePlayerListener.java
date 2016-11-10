package co.kepler.battleislands.phases.battle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.ItemUtil;
import co.kepler.battleislands.players.PlayerUtil;

public class BattlePlayerListener implements Listener {
	private PhaseBattle phaseBattle;

	public BattlePlayerListener(PhaseBattle phaseBattle) {
		this.phaseBattle = phaseBattle;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (BattleIslands.getGameConfig().getPhasesBattleClearInvOnDeath()) {
			PlayerUtil.clearInv(e.getEntity());
			ItemUtil.giveEquipment(e.getEntity());
		}
		phaseBattle.getDeathManager().playerDeath(e.getEntity());
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent e) {
		phaseBattle.getDeathManager().playerRemove(e.getPlayer());
	}
}
