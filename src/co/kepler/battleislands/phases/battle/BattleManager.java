package co.kepler.battleislands.phases.battle;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.players.BITeam;

public class BattleManager {
	private HashMap<BITeam, String> sbNames;
	private PhaseBattle phaseBattle;
	
	private Objective healthObj;
	
	public BattleManager(PhaseBattle phaseBattle) {
		this.phaseBattle = phaseBattle;
		sbNames = new HashMap<BITeam, String>();
	}
	
	public void init() {
		phaseBattle.getDeathManager().init();
		setupScoreboard();
	}
	
	private void setupScoreboard() {
		Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
		healthObj = sb.getObjective("teamHealth");
		if (healthObj != null) healthObj.unregister();
		healthObj = sb.registerNewObjective("teamHealth", "dummy");
		healthObj.setDisplayName("§6Spawn Health");
		
		int health = BattleIslands.getGameConfig().getPhasesBattleTeamHealth();
		int i = 0;
		for (BITeam team : BITeam.getTeams()) {
			sbNames.put(team, "§" + (i++) + team.getColorCode() + team.getName());
			if (BattleIslands.getPlayerManager().getPlayersOnTeam(team).size() == 0) continue;
			setTeamHealth(team, health);
		}
		healthObj.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public void setTeamHealth(BITeam team, int health) {
		if (!phaseBattle.getDeathManager().isTeamAlive(team)) return;
		health = Math.max(0, health);
		healthObj.getScore(sbNames.get(team)).setScore(health);
		if (health == 0) phaseBattle.getDeathManager().teamDeath(team);
	}
	
	public int getTeamHealth(BITeam team) {
		if (!phaseBattle.getDeathManager().isTeamAlive(team)) return 0;
		return healthObj.getScore(sbNames.get(team)).getScore();
	}
	
	public void subtractTeamHealth(BITeam team, int amount) {
		setTeamHealth(team, getTeamHealth(team) - amount);
	}
	
	public Objective getHealthObjective() {
		return healthObj;
	}
	
	public String getScoreboardName(BITeam team) {
		return sbNames.get(team);
	}
}
