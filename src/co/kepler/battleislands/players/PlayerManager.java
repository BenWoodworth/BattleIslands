package co.kepler.battleislands.players;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.phases.equipment.EquipmentInv;

public class PlayerManager implements Listener {
	private HashMap<UUID, PlayerInfo> playerInfo;
	
	public PlayerManager() {
		playerInfo = new HashMap<UUID, PlayerInfo>();
	}
	
	private PlayerInfo getPlayerInfo(UUID uuid) {
		PlayerInfo result = playerInfo.get(uuid);
		if (result == null) {
			result = new PlayerInfo();
			playerInfo.put(uuid, result);
		}
		return result;
	}
	public void removePlayerInfo(UUID uuid) {
		playerInfo.remove(uuid);
	}
	public void removeAllPlayerInfo() {
		playerInfo.clear();
	}
	
	public BITeam getTeam(UUID id) {
		return getPlayerInfo(id).team;
	}
	public void setTeam(Player p, BITeam team) {
		PlayerInfo pi = getPlayerInfo(p.getUniqueId());
		pi.team = team;
		BattleIslands.getScoreboardManager().setPlayerTeam(p, team);
	}
	
	public Set<UUID> getPlayersOnTeam(BITeam team) {
		Set<UUID> result = new HashSet<UUID>();
		for (UUID id : playerInfo.keySet()) {
			if (playerInfo.get(id).team == team)
				result.add(id);
		}
		return result;
	}
	
	public boolean isPlayerOnTeam(Player p) {
		return getPlayerInfo(p.getUniqueId()).team != BITeam.NONE;
	}
	
	private class PlayerInfo {
		private BITeam team;
		private ItemStack[] equipment;
		private PlayerInfo() {
			team = BITeam.NONE;
			equipment = new ItemStack[0];
		}
	}
	
	public void playerDisconnect(Player p) {
		UUID id = p.getUniqueId();
		PlayerConfig pc = BattleIslands.getPlayerConfig();
		pc.setLastOnline(id);
		pc.addTimeOnline(id, System.currentTimeMillis() -
				pc.loginTime.get(id));
		pc.loginTime.remove(id);
		
		EquipmentInv.disposeInv(p);
		
		File playerFile = pc.getPlayerFile(id);
		try {
			pc.getConf(id).save(playerFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		pc.configs.remove(id);
		BattleIslands.getPlayerManager().removePlayerInfo(id);
	}
	
	public ItemStack[] getPlayerEquipment(UUID id) {
		return getPlayerInfo(id).equipment;
	}
	
	public void setPlayerEquipment(UUID id, ItemStack[] is) {
		getPlayerInfo(id).equipment = is;
	}
}
