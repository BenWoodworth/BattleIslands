package co.kepler.battleislands;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import co.kepler.battleislands.listeners.ChatListener;
import co.kepler.battleislands.listeners.EffectsListener;
import co.kepler.battleislands.listeners.PlayerListener;
import co.kepler.battleislands.listeners.WeatherListener;
import co.kepler.battleislands.phases.Phase;
import co.kepler.battleislands.phases.PhaseManager;
import co.kepler.battleislands.phases.PhaseTimer;
import co.kepler.battleislands.phases.battle.PhaseBattle;
import co.kepler.battleislands.phases.end.PhaseEnd;
import co.kepler.battleislands.phases.equipment.PhaseEquipment;
import co.kepler.battleislands.phases.lobby.PhaseLobby;
import co.kepler.battleislands.players.PlayerConfig;
import co.kepler.battleislands.players.PlayerManager;
import co.kepler.battleislands.players.TeamManager;
import co.kepler.battleislands.worlds.WorldManager;



public class BattleIslands extends JavaPlugin {
	private static BattleIslands battleIslands;
	
	private File pluginDir = getDataFolder();
	private File dataDir;
	private File gameConfigFile;
	private File worldsConfigFile;
	private File itemsConfigFile;
	
	private GameConfig gameConfig;
	private PlayerConfig playerConfig;
	
	private WorldManager worldManager;
	private PhaseManager phaseManager;
	private PlayerManager playerManager;
	private TeamManager scoreboardManager;
	private PhaseTimer phaseTimer;
	
	@Override
	public void onEnable() {
		battleIslands = this;
		
		pluginDir.mkdirs();
		try {
			loadConfigsAndManagers();
		} catch (IOException e) {
			e.printStackTrace();
		}
		registerListeners();

		phaseManager.setPhases(new Phase[] {
				new PhaseLobby(),
				new PhaseEquipment(),
				new PhaseBattle(),
				new PhaseEnd()
		});
		phaseManager.nextPhase();
		
		PacketListener.register(this);
	}
	
	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers())
			playerManager.playerDisconnect(p);
	}
	
	private void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new EffectsListener(), this);
		pm.registerEvents(playerConfig, this);
		pm.registerEvents(worldManager, this);
		pm.registerEvents(phaseTimer, this);
		pm.registerEvents(new WeatherListener(), this);
		pm.registerEvents(new PlayerListener(), this);
	}
	
	private void loadConfigsAndManagers() throws IOException {
		this.saveDefaultConfig();
		dataDir = new File(this.getConfig().getString("data_directory"));
		if (!new File(dataDir, "worlds.yml").exists()) {
			getLogger().info("Worlds config not found. Extracting default worlds...");
			unzipResource("worlds.zip", dataDir);
		}
		
		itemsConfigFile = new File(dataDir, "items.yml");
		gameConfigFile = new File(dataDir, "gameConfig.yml");
		worldsConfigFile = new File(dataDir, "worlds.yml");
		
		saveResource("items.yml", itemsConfigFile, false);
		saveResource("gameConfig.yml", gameConfigFile, false);
		saveResource("worlds.yml", worldsConfigFile, false);
		
		gameConfig = new GameConfig(loadYaml(gameConfigFile));
		
		itemsConfigFile = new File(getDataDir(), "items.yml");
		saveResource("items.yml", itemsConfigFile, false);
		ItemUtil.init(loadYaml(itemsConfigFile));
		
		worldsConfigFile = new File(getDataDir(), "worlds.yml");
		saveResource("worlds.yml", worldsConfigFile, false);
		worldManager = new WorldManager(loadYaml(worldsConfigFile));
		
		playerConfig = new PlayerConfig();
		playerManager = new PlayerManager();
		
		phaseManager = new PhaseManager(this);
		
		scoreboardManager = new TeamManager();
		
		phaseTimer = new PhaseTimer();
	}
	
	private static YamlConfiguration loadYaml(File f) {
		return YamlConfiguration.loadConfiguration(f);
	}
	
	private void unzipResource(String resource, File dir) throws IOException {
		ZipInputStream zip = new ZipInputStream(this.getResource(resource));
		dir.mkdirs();
		ZipEntry entry;
		File destination;
		while ((entry = zip.getNextEntry()) != null) {
			destination = new File(dir, entry.getName());
			if (!entry.isDirectory()) {
				getLogger().info("Unzipping: " + destination);
				destination.getParentFile().mkdirs();
				destination.createNewFile();
				BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(destination));
		        byte[] bytesIn = new byte[128];
		        int read;
		        while ((read = zip.read(bytesIn)) != -1)
		            os.write(bytesIn, 0, read);
		        os.close();
			}
		}
	}
	
	private void saveResource(String resource, File dest, boolean overwrite) throws IOException {
		if (dest.exists()) {
			if (!overwrite) return;
			dest.delete();
		}
		InputStream is = this.getResource(resource);
		FileOutputStream os = new FileOutputStream(dest);
		int val;
		while ((val = is.read()) != -1)
			os.write(val);
		os.close();
	}
	
	public static BattleIslands getBattleIslands() { return battleIslands; }
	public static GameConfig getGameConfig() { return battleIslands.gameConfig; }
	public static File getDataDir() { return battleIslands.dataDir; }
	public static PhaseManager getPhaseManager() { return battleIslands.phaseManager; }
	public static WorldManager getWorldManager() { return battleIslands.worldManager; }
	public static PlayerManager getPlayerManager() { return battleIslands.playerManager; }
	public static PlayerConfig getPlayerConfig() { return battleIslands.playerConfig; }
	public static TeamManager getScoreboardManager() { return battleIslands.scoreboardManager; }
	public static PhaseTimer getPhaseTimer() { return battleIslands.phaseTimer; }
}
