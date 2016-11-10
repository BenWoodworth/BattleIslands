package co.kepler.battleislands.phases.lobby;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.phases.Phase;
import co.kepler.battleislands.players.BITeam;
import co.kepler.battleislands.players.Teleporter;

public class PhaseLobby extends Phase {
	private Listener[] listeners = new Listener[]{
			new LobbyPlayerListener(this)
	};

	private LobbyQueue queue = new LobbyQueue(this);

	@Override
	public Listener[] getListeners() {
		return listeners;
	}

	@Override
	public void startPhase() {
		try {
			BattleIslands.getWorldManager().loadLobby();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Location spawn = BattleIslands.getWorldManager().getLobbySpawn();
		Teleporter tp = new Teleporter();
		tp.queue(Bukkit.getOnlinePlayers(), spawn);
		tp.teleport(new Runnable() {
			public void run() {
				finishStartPhase();
			}
		});
	}

	private void finishStartPhase() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setGameMode(GameMode.ADVENTURE);
		}
		queue.startQueueNotification();
		startTimer(Bukkit.getOnlinePlayers().size());
	}

	@Override
	public void endPhase() {
		queue.stopQueueNotification();

		HashMap<BITeam, List<UUID>> teams = queue.getTeams();
		for (BITeam team : teams.keySet()) {
			for (UUID id : teams.get(team))
				BattleIslands.getPlayerManager().setTeam(Bukkit.getPlayer(id), team);
		}
		BattleIslands.getPhaseManager().nextPhase();
	}

	public LobbyQueue getLobbyQueue() {
		return queue;
	}

	public int getMaxTeamSize() {
		return (int)Math.ceil(Bukkit.getOnlinePlayers().size() / 4.);
	}

	public void startTimer(int onlinePlayers) {
		if (onlinePlayers >= BattleIslands.getGameConfig().getPhasesLobbyPlayersToStart())
			BattleIslands.getPhaseTimer().start(this, BattleIslands.getGameConfig().getPhasesLobbyDuration());
	}

	public void stopTimer(int onlinePlayers) {
		if (onlinePlayers < BattleIslands.getGameConfig().getPhasesLobbyPlayersToStart())
			BattleIslands.getPhaseTimer().stop();
	}
}
