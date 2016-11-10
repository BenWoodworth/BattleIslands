package co.kepler.battleislands.worlds;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import co.kepler.battleislands.BattleIslands;
import co.kepler.battleislands.players.BITeam;

public class WorldManager implements Listener {
	private final String LOBBY = "lobby", ARENA = "arena";
	private  final File LOCAL_LOBBY_DIR = new File(LOBBY);
	private final File LOCAL_ARENA_DIR = new File(ARENA);
	
	private File globalArenasDir, globalLobbyDir;
	
	private FileConfiguration worldConfig;
	
	SpawnPos lobbySpawn;
	private HashMap<BITeam, SpawnPos> arenaSpawns;
	private String arenaName, arenaCreator;
	private int arenaMaxHeight;
	
	private World lobby, arena;
	
	public WorldManager(FileConfiguration worldConfig) {
		globalArenasDir = new File(BattleIslands.getDataDir(), "arenas");
		globalLobbyDir = new File(BattleIslands.getDataDir(), "lobby");
		this.worldConfig = worldConfig;
		arenaSpawns = new HashMap<BITeam, SpawnPos>();
	}
	
	public World getLobby() {
		return lobby;
	}
	
	public Location getLobbySpawn() {
		return lobbySpawn.getLocation(lobby);
	}
	
	public void loadLobby() throws IOException {
		if (lobby != null) Bukkit.unloadWorld(lobby, false);
		if (LOCAL_LOBBY_DIR.exists()) FileUtils.deleteDirectory(LOCAL_LOBBY_DIR);
		FileUtils.copyDirectory(globalLobbyDir, LOCAL_LOBBY_DIR);
		lobby = Bukkit.createWorld(new WorldCreator(LOBBY));
		configureLobby(lobby);
		lobbySpawn = new SpawnPos(worldConfig.getDoubleList("lobby.spawn"));
	}
	
	public World getArena() {
		return arena;
	}
	
	public void loadArena(String name) throws IOException {
		if (arena != null) Bukkit.unloadWorld(arena, false);
		if (LOCAL_ARENA_DIR.exists()) FileUtils.deleteDirectory(LOCAL_ARENA_DIR);
		FileUtils.copyDirectory(getArenaDir(name), LOCAL_ARENA_DIR);
		configureArena(arena = Bukkit.createWorld(new WorldCreator(ARENA)));
		
		String path = "arenas." + name;
		arenaName = worldConfig.getString(path + ".name");
		arenaCreator = worldConfig.getString(path + ".creator");
		arenaMaxHeight = worldConfig.getInt(path + ".maxHeight");
		arenaSpawns.put(BITeam.NONE,   new SpawnPos(worldConfig.getDoubleList(path + ".spawn.spectator")));
		arenaSpawns.put(BITeam.BLUE,   new SpawnPos(worldConfig.getDoubleList(path + ".spawn.blue")));
		arenaSpawns.put(BITeam.RED,    new SpawnPos(worldConfig.getDoubleList(path + ".spawn.red")));
		arenaSpawns.put(BITeam.GREEN,  new SpawnPos(worldConfig.getDoubleList(path + ".spawn.green")));
		arenaSpawns.put(BITeam.YELLOW, new SpawnPos(worldConfig.getDoubleList(path + ".spawn.yellow")));
	}
	
	private File getArenaDir(String name) {
		return new File(globalArenasDir, name);
	}
	
	public String getArenaName() {
		return arenaName;
	}
	
	public String getArenaCreator() {
		return arenaCreator;
	}
	
	public int getArenaMaxHeight() {
		return arenaMaxHeight;
	}
	
	public Location getArenaSpawn(BITeam team) {
		return arenaSpawns.get(team).getLocation(arena);
	}
	public Location getArenaSpawn(Player player) {
		BITeam team = BattleIslands.getPlayerManager().getTeam(player.getUniqueId());
		return getArenaSpawn(team);
	}
	
	private static void configureArena(World arena) {
		arena.setAutoSave(false);
		arena.setDifficulty(Difficulty.PEACEFUL);
		arena.setKeepSpawnInMemory(true);
		arena.setPVP(true);
		arena.setTime(6000);
		
		arena.setAmbientSpawnLimit(0);
		arena.setAnimalSpawnLimit(0);
		arena.setMonsterSpawnLimit(0);
		arena.setWaterAnimalSpawnLimit(0);
		
		arena.setGameRuleValue("commandBlockOutput",  "true");
		arena.setGameRuleValue("doDaylightCycle",     "false");
		arena.setGameRuleValue("doEntityDrops",       "false");
		arena.setGameRuleValue("doFireTick",          "true");
		arena.setGameRuleValue("doMobLoot",           "false");
		arena.setGameRuleValue("doMobSpawning",       "false");
		arena.setGameRuleValue("doTileDrops",         "false");
		arena.setGameRuleValue("keepInventory",       "true");
		arena.setGameRuleValue("logAdminCommands",    "false");
		arena.setGameRuleValue("mobGriefing",         "true");
		arena.setGameRuleValue("naturalRegeneration", "true");
		arena.setGameRuleValue("randomTickSpeed",     "3");
		arena.setGameRuleValue("reducedDebugInfo",    "true");
		arena.setGameRuleValue("sendCommandFeedback", "true");
		arena.setGameRuleValue("showDeathMessages",   "false");
	}
	
	private static void configureLobby(World arena) {
		arena.setAutoSave(false);
		arena.setDifficulty(Difficulty.PEACEFUL);
		arena.setKeepSpawnInMemory(true);
		arena.setPVP(false);
		arena.setTime(6000);
		
		arena.setAmbientSpawnLimit(0);
		arena.setAnimalSpawnLimit(0);
		arena.setMonsterSpawnLimit(0);
		arena.setWaterAnimalSpawnLimit(0);
		
		arena.setGameRuleValue("commandBlockOutput",  "true");
		arena.setGameRuleValue("doDaylightCycle",     "false");
		arena.setGameRuleValue("doEntityDrops",       "false");
		arena.setGameRuleValue("doFireTick",          "false");
		arena.setGameRuleValue("doMobLoot",           "false");
		arena.setGameRuleValue("doMobSpawning",       "false");
		arena.setGameRuleValue("doTileDrops",         "false");
		arena.setGameRuleValue("keepInventory",       "true");
		arena.setGameRuleValue("logAdminCommands",    "false");
		arena.setGameRuleValue("mobGriefing",         "false");
		arena.setGameRuleValue("naturalRegeneration", "true");
		arena.setGameRuleValue("randomTickSpeed",     "3");
		arena.setGameRuleValue("reducedDebugInfo",    "true");
		arena.setGameRuleValue("sendCommandFeedback", "true");
		arena.setGameRuleValue("showDeathMessages",   "false");
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (!e.getBlock().getWorld().equals(arena)) return;
		if (e.getBlock().getY() > arenaMaxHeight)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		World world = e.getRespawnLocation().getWorld();
		if (world == lobby) {
			e.setRespawnLocation(lobbySpawn.getLocation(world));
		} else if (world == arena) {
			BITeam team = BattleIslands.getPlayerManager().getTeam(e.getPlayer().getUniqueId());
			e.setRespawnLocation(arenaSpawns.get(team).getLocation(world));
		}
	}
}
