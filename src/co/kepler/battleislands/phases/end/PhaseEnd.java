package co.kepler.battleislands.phases.end;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.listeners.ArenaListener;
import co.kepler.battleislands.phases.Phase;

public class PhaseEnd extends Phase {
	Listener[] listeners;
	
	public PhaseEnd() {
		listeners = new Listener[] {
				new ArenaListener(),
		};
	}
	
	@Override
	public Listener[] getListeners() {
		return listeners;
	}

	@Override
	public void startPhase() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getGameMode() != GameMode.SPECTATOR)
				p.setGameMode(GameMode.SPECTATOR);
		}
		BattleIslands.getPhaseTimer().start(this, BattleIslands.getGameConfig().getPhasesEndDuration());
		playPhaseStartSound();
	}

	@Override
	public void endPhase() {
		//TODO Remove
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.kickPlayer("§cRestarting server...\n§b(Should only take a few seconds)");
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
		
		//BattleIslands.getPhaseManager().nextPhase();
	}

}
