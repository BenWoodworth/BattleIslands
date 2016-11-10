package co.kepler.battleislands.phases;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import co.kepler.battleislands.BattleIslands;

public class PhaseManager {
	private BattleIslands bi;
	
	private Phase[] phases;
	private int curPhase = -1;
	
	public PhaseManager(BattleIslands bi) {
		this.bi = bi;
	}
	
	public void setPhases(Phase... phases) {
		this.phases = phases;
	}
	
	public void nextPhase() {
		PluginManager pm = Bukkit.getPluginManager();
		Phase phase;
		
		if (curPhase != -1) {
			phase = phases[curPhase];
			for (Listener l : phase.getListeners()) {
				HandlerList.unregisterAll(l);
			}
		}
		
		curPhase = (curPhase + 1) % phases.length;
		phase = phases[curPhase];
		phase.startPhase();
		for (Listener l : phase.getListeners()) {
			pm.registerEvents(l, bi);
		}
	}
	
	public BattleIslands getBattleIslands() {
		return bi;
	}
}
