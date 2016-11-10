package co.kepler.battleislands.players;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamManager {
	private Scoreboard scoreboard;
	
	private HashMap<BITeam, Team> teams;
	
	public TeamManager() {
		scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		
		for (Team t : scoreboard.getTeams()) t.unregister();
		for (Objective o : scoreboard.getObjectives()) o.unregister();
		
		teams = new HashMap<BITeam, Team>();
		teams.put(BITeam.BLUE, scoreboard.registerNewTeam(BITeam.BLUE.getName()));
		teams.put(BITeam.RED, scoreboard.registerNewTeam(BITeam.RED.getName()));
		teams.put(BITeam.GREEN, scoreboard.registerNewTeam(BITeam.GREEN.getName()));
		teams.put(BITeam.YELLOW, scoreboard.registerNewTeam(BITeam.YELLOW.getName()));
		teams.put(BITeam.NONE, scoreboard.registerNewTeam(BITeam.NONE.getName()));
		
		Team curTeam;
		for (BITeam biteam : teams.keySet()) {
			curTeam = teams.get(biteam);
			curTeam.setPrefix(biteam.getColorCode());
			curTeam.setCanSeeFriendlyInvisibles(true);
			curTeam.setAllowFriendlyFire(false);
		}
	}
	
	public Team getTeam(BITeam team) {
		return teams.get(team);
	}
	
	@SuppressWarnings("deprecation")
	public void setPlayerTeam(OfflinePlayer p, BITeam biteam) {
		teams.get(biteam).addPlayer(p);
	}
	
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
}
