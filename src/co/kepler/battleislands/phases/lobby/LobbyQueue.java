package co.kepler.battleislands.phases.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.nms.PacketUtil;
import co.kepler.battleislands.players.BITeam;

public class LobbyQueue implements Runnable {
	private PhaseLobby phaseLobby;
	private HashMap<BITeam, Queue<UUID>> queues;

	public LobbyQueue(PhaseLobby phaseLobby) {
		this.phaseLobby = phaseLobby;
		queues = new HashMap<BITeam, Queue<UUID>>();
		queues.put(BITeam.BLUE, new LinkedList<UUID>());
		queues.put(BITeam.RED, new LinkedList<UUID>());
		queues.put(BITeam.GREEN, new LinkedList<UUID>());
		queues.put(BITeam.YELLOW, new LinkedList<UUID>());
	}

	public void queuePlayer(Player p, BITeam team) {
		Queue<UUID> addTo = queues.get(team);
		if (addTo.contains(p.getUniqueId())) return;
		for (Queue<UUID> queue : queues.values())
			queue.remove(p.getUniqueId());
		addTo.add(p.getUniqueId());
		p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1F, 1F);

		if (BattleIslands.getGameConfig().getPhasesLobbyShowTitleOnQueue()) {
			String color = team.getColorCode();
			String title = "{text:\"" + color + "Added to " + team.getName() + " queue\"}";
			String subtitle = "{text:\"" + color + "Position: " + addTo.size() + "/" + phaseLobby.getMaxTeamSize() + "\"}";
			PacketUtil.showTitle(title, subtitle, 10, 40, 20, p);
		}
		showQueueNotification(p, team, addTo.size());
		BattleIslands.getScoreboardManager().setPlayerTeam(p,  team);
	}

	public void dequeue(Player p) {
		for (Queue<UUID> queue : queues.values())
			queue.remove(p.getUniqueId());
		BattleIslands.getScoreboardManager().setPlayerTeam(p, BITeam.NONE);
	}

	public void clearQueue() {
		for (Queue<UUID> queue : queues.values())
			queue.clear();
	}

	public HashMap<BITeam, List<UUID>> getTeams() {
		int max = phaseLobby.getMaxTeamSize();
		List<UUID> teamBlue = new ArrayList<UUID>();
		List<UUID> teamRed = new ArrayList<UUID>();
		List<UUID> teamGreen = new ArrayList<UUID>();
		List<UUID> teamYellow = new ArrayList<UUID>();

		Queue<UUID> queueBlue = new LinkedList<UUID>(queues.get(BITeam.BLUE));
		Queue<UUID> queueRed = new LinkedList<UUID>(queues.get(BITeam.RED));
		Queue<UUID> queueGreen = new LinkedList<UUID>(queues.get(BITeam.GREEN));
		Queue<UUID> queueYellow = new LinkedList<UUID>(queues.get(BITeam.YELLOW));

		for (int i = 0; i < max; i++) {
			if (!queueBlue.isEmpty())   teamBlue.add(queueBlue.poll());
			if (!queueRed.isEmpty())    teamRed.add(queueRed.poll());
			if (!queueGreen.isEmpty())  teamGreen.add(queueGreen.poll());
			if (!queueYellow.isEmpty()) teamYellow.add(queueYellow.poll());
		}

		Queue<UUID> remaining = new LinkedList<UUID>();
		for (Player p : Bukkit.getOnlinePlayers())
			remaining.add(p.getUniqueId());
		remaining.removeAll(teamBlue);
		remaining.removeAll(teamRed);
		remaining.removeAll(teamGreen);
		remaining.removeAll(teamYellow);

		@SuppressWarnings("unchecked")
		List<UUID>[] teams = new List[] {teamBlue, teamRed, teamGreen, teamYellow};
		while (!remaining.isEmpty()) smallest(teams).add(remaining.poll());
		
		HashMap<BITeam, List<UUID>> result = new HashMap<BITeam, List<UUID>>();
		result.put(BITeam.BLUE, teamBlue);
		result.put(BITeam.RED, teamRed);
		result.put(BITeam.GREEN, teamGreen);
		result.put(BITeam.YELLOW, teamYellow);
		return result;
	}

	int taskID;
	public void startQueueNotification() {
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(
				BattleIslands.getBattleIslands(), this, 0, 20);
	}

	public void stopQueueNotification() {
		Bukkit.getScheduler().cancelTask(taskID);
	}

	public void run() {
		Queue<UUID> queue;
		int i;
		for (BITeam t : queues.keySet()) {
			queue = queues.get(t);
			i = 1;
			for (UUID u : queue) {
				showQueueNotification(Bukkit.getPlayer(u), t, i++);
			}
		}
	}

	public void showQueueNotification(Player p, BITeam team, int pos) {
		PacketUtil.sendActionBarMessage("{text:\"" +
				team.getColorCode() + "§lQueued for " + team.getName() + " (" +
				"Position: " + Integer.toString(pos) + "/" + Integer.toString(phaseLobby.getMaxTeamSize()) + ")" +
				"\"}", p);
	}

	@SuppressWarnings("unchecked")
	private List<UUID> smallest(@SuppressWarnings("rawtypes") List[] lists) {
		int smallest = -1;
		List<List<UUID>> smallestLists = new ArrayList<List<UUID>>();
		for (List<UUID> l : lists) {
			if (smallest == -1 || l.size() < smallest) {
				smallest = l.size();
				smallestLists.clear();
				smallestLists.add(l);
			} else if (l.size() == smallest) {
				smallestLists.add(l);
			}
		}
		return smallestLists.get((int)(Math.random() * smallestLists.size()));
	}
}
