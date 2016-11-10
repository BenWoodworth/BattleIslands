package co.kepler.battleislands.phases;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Phase {
	public abstract Listener[] getListeners();
	
	public abstract void startPhase();
	
	public abstract void endPhase();
	
	public void playPhaseStartSound() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1F, 1F);
		}
	}
}
