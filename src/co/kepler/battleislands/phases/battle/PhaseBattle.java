package co.kepler.battleislands.phases.battle;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.ItemUtil;
import co.kepler.battleislands.listeners.ArenaListener;
import co.kepler.battleislands.nms.PacketUtil;
import co.kepler.battleislands.phases.Phase;
import co.kepler.battleislands.players.BITeam;
import co.kepler.battleislands.worlds.WorldManager;

public class PhaseBattle extends Phase {
	private Listener[] listeners = new Listener[] {
				new ArenaListener(),
				new BattleInvListener(),
				new BattlePlayerListener(this)
		};
	
	private BattleManager battleManager = new BattleManager(this);
	private BattleTimer battleTimer = new BattleTimer(this);
	private BattleDeathManager deathManager = new BattleDeathManager(this);

	@Override
	public Listener[] getListeners() {
		return listeners;
	}

	@Override
	public void startPhase() {
		battleManager.init();
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!BattleIslands.getPlayerManager().isPlayerOnTeam(p)) continue;
			ItemUtil.giveEquipment(p);
		}
		BattleIslands.getPhaseTimer().start(this, BattleIslands.getGameConfig().getPhasesBattleDuration());
		battleTimer.start();
	}

	@Override
	public void endPhase() {
		battleTimer.stop();

		List<BITeam> winners = deathManager.getLivingTeams();
		String title, subtitle;
		if (winners.size() == 0) {
			title = "§7There are no winners!";
			subtitle = "(This is probably a bug...)";
		} else if (winners.size() == 1) {
			BITeam t = winners.get(0);
			title = t.getColorCode() + "Game Over!";
			subtitle = t.getColorCode() + t.getName() + " team wins!";
		} else {
			title = "§7Game Over!";
			subtitle = "";
			for (int i = 0; i < winners.size(); i++) {
				BITeam t = winners.get(i);
				if (i == 0) {
					subtitle += t.getColorCode() + t.getName();
				} else if (i == winners.size() - 1) {
					subtitle += "§7 and " + t.getColorCode() + t.getName();
				} else {
					subtitle += "§7, " + t.getColorCode() + t.getName();
				}
			}
			subtitle += "§7 win!";
		}
		PacketUtil.showTitle("{text:\"" + title + "\"}",
				"{text:\"" + subtitle + "\"}", 30, 100, 30,
				Bukkit.getOnlinePlayers().toArray(new Player[0]));

		spawnFireworks(winners);
		
		
		BattleIslands.getPhaseManager().nextPhase();
	}

	public void spawnFireworks(List<BITeam> teams) {
		WorldManager wm = BattleIslands.getWorldManager();
		
		for (BITeam team : teams) {
			FireworkEffect.Builder builder = FireworkEffect.builder().withColor(team.getColor());
			
			Firework fw = (Firework)wm.getArena().spawnEntity( wm.getArenaSpawn(team), EntityType.FIREWORK);
			fw.getLocation().add(0, 2, 0);
			FireworkMeta fm = fw.getFireworkMeta();
			fm.addEffect(builder.with(Type.BALL_LARGE).build());
		}
	}

	public BattleManager getBattleManager() {
		return battleManager;
	}

	public BattleDeathManager getDeathManager() {
		return deathManager;
	}

	public static Block getTopWoolBlock(Player p) {
		return getTopWoolBlock(p.getWorld(), p.getLocation().getBlockX(),
				p.getLocation().getBlockY(), p.getLocation().getBlockZ());
	}
	public static Block getTopWoolBlock(World w, int x, int y, int z) {
		Block b;
		for (; y >= 0; y--) {
			b = w.getBlockAt(x, y, z);
			if (b.getType() == Material.WOOL) return b;
		}
		return null;
	}

	public static BITeam getBaseStandingOn(Player p) {
		return BITeam.fromWool(getTopWoolBlock(p));
	}
}
