package co.kepler.battleislands.listeners;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.phases.battle.PhaseBattle;
import co.kepler.battleislands.players.BITeam;
import co.kepler.battleislands.players.Teleporter;

public class ArenaListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.setGameMode(GameMode.SPECTATOR);
		BattleIslands.getPlayerManager().setTeam(p, BITeam.NONE);
		Teleporter.teleport(p, BattleIslands.getWorldManager().getArenaSpawn(p));
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(BattleIslands.getWorldManager().getArenaSpawn(e.getPlayer()));
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		for (int i = e.blockList().size() - 1; i >= 0; --i) {
			switch (e.blockList().get(i).getType()) {
			case STAINED_CLAY:
			case TNT:
				break;
			default:
				e.blockList().remove(i);
			}
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		switch (e.getEntityType()) {
		case ARROW:
			((CraftArrow)e.getEntity()).getHandle().fromPlayer = 0;
			break;
		case ENDER_PEARL:
			if (!(e.getEntity().getShooter() instanceof Player)) break;
			e.getEntity().setPassenger((Player)e.getEntity().getShooter());
			break;
		default:
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (PhaseBattle.getTopWoolBlock(e.getBlock().getWorld(),
				e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()) != null) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getCause() == DamageCause.VOID) {
			e.setDamage(1000000);
		}
	}
}
