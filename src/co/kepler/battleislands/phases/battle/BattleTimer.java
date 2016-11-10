package co.kepler.battleislands.phases.battle;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.ItemUtil;
import co.kepler.battleislands.ItemUtil.BattleItemInfo;
import co.kepler.battleislands.players.BITeam;

public class BattleTimer implements Runnable {
	private int taskID = -1;
	private PhaseBattle phaseBattle;

	private HashMap<ItemStack, BattleItemInfo> battleItemInfo;
	private HashMap<ItemStack, Integer> battleItemTimeRemaining;
	private HashMap<ItemStack, HashMap<BITeam, ItemStack>> coloredItems;
	
	public BattleTimer(PhaseBattle phaseBattle) {
		this.phaseBattle = phaseBattle;
		battleItemInfo = ItemUtil.getBattleItemTimings();
		battleItemTimeRemaining = new HashMap<ItemStack, Integer>();
		coloredItems = new HashMap<ItemStack, HashMap<BITeam, ItemStack>>();
		for (ItemStack is : battleItemInfo.keySet()) {
			battleItemTimeRemaining.put(is, battleItemInfo.get(is).seconds);
			HashMap<BITeam, ItemStack> newMap = new HashMap<BITeam, ItemStack>();
			for (BITeam team : BITeam.getTeams()) {
				newMap.put(team, ItemUtil.colorItem(is, team));
			}
			coloredItems.put(is, newMap);
		}
	}

	public void start() {
		if (taskID != -1) return;
		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(
				BattleIslands.getBattleIslands(), this, 0, 20);

	}

	public void stop() {
		if (taskID == -1) return;
		Bukkit.getScheduler().cancelTask(taskID);
		taskID = -1;
	}
	
	public void run() {
		runItemTimer();
		runHealthTimer();
	}
	
	public void runItemTimer() {
		for (ItemStack is : battleItemTimeRemaining.keySet()) {
			int time = battleItemTimeRemaining.get(is);
			if (time <= 0) {
				time = battleItemInfo.get(is).seconds;
				giveItems(is);
			}
			battleItemTimeRemaining.put(is, time - 1);
		}
	}
	
	private void giveItems(ItemStack is) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!BattleIslands.getPlayerManager().isPlayerOnTeam(p)) continue;
			BITeam team = BattleIslands.getPlayerManager().getTeam(p.getUniqueId());
			ItemStack toAdd = coloredItems.get(is).get(team);
			int max = battleItemInfo.get(is).max;
			int count = countItems(toAdd, p.getInventory());
			int size = Math.min(max - count, toAdd.getAmount());
			if (size > 0) {
				toAdd.setAmount(size);
				p.getInventory().addItem(toAdd);
			}
		}
	}

	private int countItems(ItemStack is, Inventory inv) {
		int result = 0;
		for (ItemStack test : inv.getContents()) {
			if (is.isSimilar(test)) result += test.getAmount();
		}
		return result;
	}

	public void runHealthTimer() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!phaseBattle.getDeathManager().isPlayerAlive(p)) continue;
			if (!BattleIslands.getPlayerManager().isPlayerOnTeam(p)) continue;
			BITeam playerTeam = BattleIslands.getPlayerManager().getTeam(p.getUniqueId());
			BITeam baseTeam = PhaseBattle.getBaseStandingOn(p);
			if (baseTeam != null && playerTeam != baseTeam) phaseBattle.getBattleManager().subtractTeamHealth(baseTeam, 1);
		}
	}
}
