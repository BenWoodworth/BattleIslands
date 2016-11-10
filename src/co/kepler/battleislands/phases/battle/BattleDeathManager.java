package co.kepler.battleislands.phases.battle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.nms.PacketUtil;
import co.kepler.battleislands.players.BITeam;
import co.kepler.battleislands.players.PlayerUtil;

public class BattleDeathManager {
	private Set<UUID> livingPlayers;
	private Set<BITeam> livingTeams;
	PhaseBattle phaseBattle;

	public BattleDeathManager(PhaseBattle phaseBattle) {
		this.phaseBattle = phaseBattle;
		livingPlayers = new HashSet<UUID>();
		livingTeams = new HashSet<BITeam>();
	}

	public void init() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (BattleIslands.getPlayerManager().isPlayerOnTeam(p)) {
				livingPlayers.add(p.getUniqueId());
				livingTeams.add(getTeam(p.getUniqueId()));
			}
		}
	}

	private BITeam getTeam(UUID id) {
		return BattleIslands.getPlayerManager().getTeam(id);
	}

	public void playerDeath(Player p) {
		if (!isPlayerAlive(p)) return;
		BITeam team = BattleIslands.getPlayerManager().getTeam(p.getUniqueId());
		p.teleport(BattleIslands.getWorldManager().getArenaSpawn(team));
		if (!isTeamAlive(team)) {
			playerRemove(p);
			PacketUtil.showTitle("{text:\"" + team.getColorCode() + "Game Over!",
					null, 30, 100, 30, p);
		}
	}

	public void playerRemove(Player p) {
		livingPlayers.remove(p.getUniqueId());
		p.setGameMode(GameMode.SPECTATOR);
		PlayerUtil.clearInv(p);
		BITeam team = getTeam(p.getUniqueId());
		if (!teamHasLivingPlayers(team)) {
			teamLost(team);
		}
	}

	public void teamDeath(BITeam team) {
		if (!livingTeams.contains(team)) return;
		livingTeams.remove(team);
		Location l = BattleIslands.getWorldManager().getArenaSpawn(team);
		l.getWorld().playSound(l, Sound.EXPLODE, 4F, 1F);
		l.getWorld().playEffect(l, Effect.EXPLOSION_HUGE, 4, 10);
	}

	private boolean teamHasLivingPlayers(BITeam team) {
		for (UUID id : livingPlayers) {
			if (getTeam(id) == team) {
				return true;
			}
		}
		return false;
	}

	public boolean isPlayerAlive(Player p) {
		return livingPlayers.contains(p.getUniqueId());
	}

	public boolean isTeamAlive(BITeam team) {
		return livingTeams.contains(team);
	}
	public boolean isTeamAlive(Player p) {
		return livingTeams.contains(BattleIslands.getPlayerManager().getTeam(p.getUniqueId()));
	}

	public List<BITeam> getLivingTeams() {
		List<BITeam> result = new ArrayList<BITeam>();
		for (BITeam t : BITeam.getTeams()) {
			if (isTeamAlive(t)) result.add(t);
		}
		return result;
	}

	public void teamLost(BITeam team) {
		if (phaseBattle.getBattleManager().getTeamHealth(team) != 0) {
			teamDeath(team);
		}
		if (getLivingTeams().size() <= 1) {
			BattleIslands.getPhaseTimer().elapse();
		}
	}
}
