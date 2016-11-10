package co.kepler.battleislands.players;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import co.kepler.battleislands.BattleIslands;


public class Teleporter {
	private Queue<TpData> queue;
	private int taskID;

	public Teleporter() {
		queue = new LinkedList<TpData>();
		taskID = -1;
	}

	public void queue(Player p, Location l) {
		queue.add(new TpData(p, l));
	}
	
	public void queue(Player[] players, Location l) {
		for (Player p : players) {
			if (p.getWorld().equals(l.getWorld())) continue;
			queue.add(new TpData(p, l));
		}
	}
	
	public void queue(Collection<? extends Player> players, Location l) {
		for (Player p : players) {
			if (p.getWorld().equals(l.getWorld())) continue;
			queue.add(new TpData(p, l));
		}
	}

	public void teleport(final Runnable r) {
		if (taskID != -1) return;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BattleIslands.getBattleIslands(), new Runnable() {
			public void run() {
				if (queue.isEmpty()) {
					Bukkit.getScheduler().cancelTask(taskID);
					if (r != null) r.run();
				} else {
					TpData data = queue.poll();
					if (data.player.isOnline()) {
						data.player.teleport(data.location);
					}
				}
			}
		}, 1L, 1L);
	}

	private class TpData {
		public final Player player;
		public final Location location;
		public TpData(Player player, Location location) {
			this.player = player;
			this.location = location;
		}
	}

	public static void teleport(final Player p, final Location l) {
		if (p.getWorld().equals(l.getWorld())) {
			p.teleport(l);
			return;
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(BattleIslands.getBattleIslands(),
				new Runnable() {
			public void run() {
				p.teleport(l);
			}
		}, 1L);
	}
}
