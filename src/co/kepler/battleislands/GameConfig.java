package co.kepler.battleislands;

import org.bukkit.configuration.file.FileConfiguration;

public class GameConfig {
	private FileConfiguration config;
	
	public GameConfig(FileConfiguration config) {
		this.config = config;
	}
	
	public int getPhasesLobbyDuration()
	{ return config.getInt("phases.lobby.duration") * 20; }
	public int getPhasesLobbyPlayersToStart()
	{ return config.getInt("phases.lobby.playersToStart"); }
	public boolean getPhasesLobbyShowTitleOnQueue()
	{ return config.getBoolean("phases.lobby.showTitleOnQueue"); }
	
	
	public int getPhasesEquipmentDuration()
	{ return config.getInt("phases.equipment.duration") * 20; }
	
	
	public int getPhasesBattleDuration()
	{ return config.getInt("phases.battle.duration") * 20; }
	public int getPhasesBattleTeamHealth()
	{ return config.getInt("phases.battle.teamHealth"); }
	public boolean getPhasesBattleClearInvOnDeath()
	{ return config.getBoolean("phases.battle.clearInvOnDeath"); }
	
	
	public int getPhasesEndDuration()
	{ return config.getInt("phases.end.duration") * 20; }
}
