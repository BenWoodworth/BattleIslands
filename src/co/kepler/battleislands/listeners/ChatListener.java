package co.kepler.battleislands.listeners;

import java.util.Calendar;
import java.util.UUID;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.players.PlayerConfig;
import co.kepler.battleislands.players.PlayerManager;

public class ChatListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if (e.isCancelled()) return;
		Player p = e.getPlayer();
		UUID id = p.getUniqueId();
		PlayerManager pm = BattleIslands.getPlayerManager();
		PlayerConfig pc = BattleIslands.getPlayerConfig();
		

		TextComponent message = new TextComponent();
		Bukkit.getLogger().info(p.getDisplayName());
		message.setText(pm.getTeam(p.getUniqueId()).getColorCode() + p.getDisplayName());
		message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
				new TextComponent(
				pm.getTeam(p.getUniqueId()).getColorCode() + p.getName() + "\n" +
				"§aJoined§7:    §b" +   date(pc.getFirstOnline(id)) + "\n" +
				"§aPlaytime§7:  §b" +   duration(pc.getTimeOnline(id) + pc.timeSinceLogin(id)) + "\n" +
				"§aGames§7:    §b" +    i(pc.getStatGames(id)) + "\n" +
				"§aKills§7:       §b" + i(pc.getStatKills(id)) + "\n" +
				"§aDeaths§7:   §b" +    i(pc.getStatDeaths(id))
		)}));
		message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + p.getName() + " "));
		message.addExtra("§8: §r" + e.getMessage());
		for (Player pl : e.getRecipients())
			pl.spigot().sendMessage(message);
		
		e.setFormat("%s: %s");
		e.getRecipients().clear();
	}
	
	private String i(int i) {
		return Integer.toString(i);
	}
	
	private String date(long t) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t);
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toString(c.get(Calendar.MONTH) - Calendar.JANUARY + 1)).append('/');
		sb.append(Integer.toString(c.get(Calendar.DAY_OF_MONTH))).append('/');
		sb.append(Integer.toString(c.get(Calendar.YEAR)));
		return sb.toString();
	}
	
	private String duration(long t) {
		int mins = (int)(t / 60000L);
		int hours = mins / 60; mins -= hours * 60;
		int days = hours / 24; hours -= days * 24;
		StringBuilder sb = new StringBuilder();
		if (days != 0) sb.append(Integer.toString(days)).append("d ");
		if (hours != 0 || days != 0) sb.append(Integer.toString(hours)).append("h ");
		sb.append(Integer.toString(mins )).append("m");
		return sb.toString();
	}
}
