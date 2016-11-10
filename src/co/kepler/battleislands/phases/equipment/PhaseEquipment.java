package co.kepler.battleislands.phases.equipment;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.ItemUtil;
import co.kepler.battleislands.listeners.ArenaListener;
import co.kepler.battleislands.nms.PacketUtil;
import co.kepler.battleislands.phases.Phase;
import co.kepler.battleislands.players.PlayerUtil;
import co.kepler.battleislands.players.Teleporter;

public class PhaseEquipment extends Phase {
	private Listener[] listeners = new Listener[] {
			new EquipmentInvListener(),
			new ArenaListener()
	};

	@Override
	public Listener[] getListeners() {
		return listeners;
	}

	@Override
	public void startPhase() {
		try {
			BattleIslands.getWorldManager().loadArena("arena0"); //TODO Cycle through arenas
		} catch (IOException e) {
			e.printStackTrace();
		}

		Teleporter tp = new Teleporter();
		for (Player p : Bukkit.getOnlinePlayers()) {
			tp.queue(p, BattleIslands.getWorldManager().getArenaSpawn(p));
		}
		tp.teleport(new Runnable() {
			public void run() {
				finishStartPhase();
			}
		});
	}

	private void finishStartPhase() {
		int duration = BattleIslands.getGameConfig().getPhasesEquipmentDuration();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (BattleIslands.getPlayerManager().isPlayerOnTeam(p)) {
				EquipmentInv.getEquipmentInv(p).setupPlayerInv();
				String color = BattleIslands.getPlayerManager().getTeam(p.getUniqueId()).getColorCode();
				int fade = 30;
				PacketUtil.showTitle(
						"{text:\"" + color + "§lOpen your inventory\"}",
						"{text:\"" + color + "to select your equipment for battle\"}",
						fade, duration - 2 * fade, fade, p);
			}
		}
		BattleIslands.getPhaseTimer().start(this, duration);
		playPhaseStartSound();
	}

	@Override
	public void endPhase() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!BattleIslands.getPlayerManager().isPlayerOnTeam(p)) continue;
			ItemStack[] equipment = ItemUtil.getSelectedEquipment(p);
			BattleIslands.getPlayerManager().setPlayerEquipment(p.getUniqueId(), equipment);
			PlayerUtil.clearInv(p);
			p.closeInventory();
		}
		BattleIslands.getPhaseManager().nextPhase();
	}

}
