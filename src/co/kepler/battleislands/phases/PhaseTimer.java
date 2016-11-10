package co.kepler.battleislands.phases;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.nms.PacketUtil;

public class PhaseTimer implements Listener, Runnable {
	private int period = 5;
	
	private Phase phase;
	private int taskID;
	
	private int totalTicks, ellapsedTicks;
	
	public PhaseTimer() {
		ellapsedTicks = -1;
	}
	
	public void start(Phase phase, int totalTicks) {
		if (totalTicks < 0) return;
		if (ellapsedTicks != -1) return;
		period = totalTicks <= 1200 ? 5 : 20;
		this.phase = phase;
		this.ellapsedTicks = 0;
		this.totalTicks = totalTicks;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(
				BattleIslands.getBattleIslands(), this, 0, period);
	}
	
	public void stop() {
		if (ellapsedTicks == -1) return;
		ellapsedTicks = -1;
		Bukkit.getScheduler().cancelTask(taskID);
	}

	public void elapse() {
		if (ellapsedTicks == -1) return;
		stop();
		phase.endPhase();
	}
	
	public void run() {
		ellapsedTicks += period;
		updatePlayers(Bukkit.getOnlinePlayers().toArray(new Player[0]));
		
		int remaining = totalTicks - ellapsedTicks;
		if ((remaining + period) % 20 == 0 && remaining / 20 <= 4) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.playSound(p.getLocation(), Sound.CLICK, 1F, 1F);
			}
		}
		
		if (ellapsedTicks >= totalTicks) {
			elapse();
		}
	}
	
	private void updatePlayers(Player... players) {
		if (ellapsedTicks == -1) return;
		int level = (totalTicks - ellapsedTicks) / 20 + 1;
		float exp = (float)ellapsedTicks / totalTicks;
		if (level > 60) level = (int)Math.ceil(level / 60.);
		PacketUtil.setExp(exp, level, players);
	}
	
	public void setPeriod(int totalTicks) {
		int secs = totalTicks / 20;
		if (secs <= 5)       period = 5;
		else if (secs <= 10) period = 10;
		else                 period = 20;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		updatePlayers(e.getPlayer());
	}
}
