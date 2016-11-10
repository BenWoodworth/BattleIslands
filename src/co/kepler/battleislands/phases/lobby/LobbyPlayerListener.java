package co.kepler.battleislands.phases.lobby;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.players.BITeam;
import co.kepler.battleislands.players.PlayerUtil;
import co.kepler.battleislands.players.Teleporter;

public class LobbyPlayerListener implements Listener {
	private PhaseLobby phaseLobby;
	
	public LobbyPlayerListener(PhaseLobby phaseLobby) {
		this.phaseLobby = phaseLobby;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().setGameMode(GameMode.ADVENTURE);
		Teleporter.teleport(e.getPlayer(), BattleIslands.getWorldManager().getLobbySpawn());
		BattleIslands.getPlayerManager().setTeam(e.getPlayer(), BITeam.NONE);
		PlayerUtil.clearInv(e.getPlayer());
		phaseLobby.startTimer(Bukkit.getOnlinePlayers().size());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		phaseLobby.getLobbyQueue().dequeue(e.getPlayer());
		phaseLobby.stopTimer(Bukkit.getOnlinePlayers().size() - 1);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(BattleIslands.getWorldManager().getLobbySpawn());
	}
	
	@EventHandler
	@SuppressWarnings("deprecation")
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getTo().getY() <= 0) {
			e.getPlayer().teleport(BattleIslands.getWorldManager().getLobbySpawn());
			e.getPlayer().setFallDistance(0);
			return;
		}
		Block b = e.getTo().clone().add(0, -1, 0).getBlock();
		if (!b.getType().equals(Material.WOOL)) return;
		BITeam team;
		switch (DyeColor.getByWoolData(b.getData())) {
		case BLUE: team = BITeam.BLUE; break;
		case RED: team = BITeam.RED; break;
		case LIME: team = BITeam.GREEN; break;
		case YELLOW: team = BITeam.YELLOW; break;
		default: return;
		}
		phaseLobby.getLobbyQueue().queuePlayer(e.getPlayer(), team);
	}
}
