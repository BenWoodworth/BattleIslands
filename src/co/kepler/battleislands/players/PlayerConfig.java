package co.kepler.battleislands.players;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.ItemUtil;

public class PlayerConfig implements Listener {
	public HashMap<UUID, FileConfiguration> configs;
	public HashMap<UUID, Long> loginTime;
	private File playersDir;
	
	public PlayerConfig() {
		configs = new HashMap<UUID, FileConfiguration>();
		loginTime = new HashMap<UUID, Long>();
		playersDir = new File(BattleIslands.getDataDir(), "Players");
	}
	
	public File getPlayerFile(UUID id) {
		return new File(playersDir, id + ".yml");
	}
	
	public FileConfiguration getConf(UUID id) {
		FileConfiguration result = configs.get(id);
		if (result != null) return result;

		File playerFile = getPlayerFile(id);
		try {
			playerFile.getParentFile().mkdirs();
			playerFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		result = YamlConfiguration.loadConfiguration(playerFile);
		configs.put(id, result);
		return result;
	}
	
	private int getInt(UUID id, String key, int def)
	{ return getConf(id).getInt(key, def); }
	private void setInt(UUID id, String key, int value)
	{ getConf(id).set(key, value); }
	private void addInt(UUID id, String key, int def, int amount)
	{ setInt(id, key, getInt(id, key, def) + amount); }
	
	private long getLong(UUID id, String key, long def)
	{ return getConf(id).getLong(key, def); }
	private void setLong(UUID id, String key, long value)
	{ getConf(id).set(key, value); }
	private void addLong(UUID id, String key, long def, long amount) {
		FileConfiguration c = getConf(id);
		c.set(key, c.getLong(key, def) + amount);
	}
	
	private String getString(UUID id, String key, String def)
	{ return getConf(id).getString(key, def); }
	private void setString(UUID id, String key, String value)
	{ getConf(id).set(key, value); }
	
	
	//---------------- Name ----------------//
	public String getNickname(UUID id)
	{ return getString(id, "name.nickname", Bukkit.getPlayer(id).getName()); }
	public void setNickname(UUID id, String nickname)
	{ setString(id, "name.nickname", nickname); }
	
	public void setUsername(UUID id)
	{ setString(id, "name.username", Bukkit.getPlayer(id).getName()); }
	
	
	//---------------- Statistics ----------------//
	public int getStatKills(UUID id)
	{ return getInt(id, "stats.kills", 0); }
	public void addStatKills(UUID id)
	{ addInt(id, "stats.kills", 0, 1); }
	
	public int getStatDeaths(UUID id)
	{ return getInt(id, "stats.deaths", 0); }
	public void addStatDeaths(UUID id)
	{ addInt(id, "stats.deaths", 0, 1); }
	
	public int getStatGames(UUID id)
	{ return getInt(id, "stats.games", 0); }
	public void addStatGames(UUID id)
	{ addInt(id, "stats.games", 0, 1); }

	
	//---------------- Details ----------------//
	public long getFirstOnline(UUID id)
	{ return getLong(id, "details.first_online", -1L); }
	public void setFirstOnline(UUID id) {
		if (getFirstOnline(id) != -1L) return;
		setLong(id, "details.first_online", System.currentTimeMillis());
	}

	public long getLastOnline(UUID id)
	{ return getLong(id, "details.last_online", -1L); }
	public void setLastOnline(UUID id)
	{ setLong(id, "details.last_online", System.currentTimeMillis()); }
	
	public long getTimeOnline(UUID id)
	{ return getLong(id, "details.time_online", 0); }
	public void addTimeOnline(UUID id, long time)
	{ addLong(id, "details.time_online", 0, time); }
	
	public long timeSinceLogin(UUID id) {
		Long l = loginTime.get(id);
		if (l == null) return 0L;
		return System.currentTimeMillis() - l;
	}
	
	//---------------- Preferences ----------------//
	public int[] getEquipmentPreferences(UUID id) {
		List<Integer> ints = getConf(id).getIntegerList("preferences.equipment");
		int size = ItemUtil.getNumberOfItems();
		if (ints.size() != size) return new int[size];
		int[] result = new int[ints.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = ints.get(i);
		}
		return result;
	}
	
	public void setEquipmentPreferences(UUID id, int[] preferences) {
		getConf(id).set("preferences.equipment", preferences);
	}
	
	//---------------- Event Listeners ----------------//
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		UUID id = e.getPlayer().getUniqueId();
		loginTime.put(id, System.currentTimeMillis());
		
		setFirstOnline(id);
		setLastOnline(id);
		setUsername(id);
		e.getPlayer().setDisplayName(getNickname(id));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		BattleIslands.getPlayerManager().playerDisconnect(e.getPlayer());
	}
}
