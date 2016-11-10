package co.kepler.battleislands.players;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.material.Wool;

public enum BITeam {
	BLUE, RED, GREEN, YELLOW, NONE;
	
	public String getName() {
		switch (this) {
		case BLUE: return "Blue";
		case RED: return "Red";
		case GREEN: return "Green";
		case YELLOW: return "Yellow";
		default: return "None";
		}
	}
	
	public String getColorCode() {
		switch (this) {
		case BLUE: return "§b";
		case RED: return "§c";
		case GREEN: return "§a";
		case YELLOW: return "§e";
		default: return "§7";
		}
	}
	
	public Color getLeatherColor() {
		switch (this) {
		case BLUE: return Color.AQUA;
		case RED: return Color.RED;
		case GREEN: return Color.LIME;
		case YELLOW: return Color.YELLOW;
		default: return Color.GRAY;
		}
	}
	
	public DyeColor getDyeColor() {
		switch (this) {
		case BLUE: return DyeColor.BLUE;
		case RED: return DyeColor.RED;
		case GREEN: return DyeColor.LIME;
		case YELLOW: return DyeColor.YELLOW;
		default: return DyeColor.GRAY;
		}
	}
	
	public Color getColor() {
		switch (this) {
		case BLUE: return Color.BLUE;
		case RED: return Color.RED;
		case GREEN: return Color.LIME;
		case YELLOW: return Color.YELLOW;
		default: return Color.GRAY;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static BITeam fromWool(Block b) {
		if (b == null) return null;
		Wool w = new Wool(b.getType(), b.getData());
		switch (w.getColor()) {
		case BLUE: return BLUE;
		case RED: return RED;
		case LIME: return GREEN;
		case YELLOW: return YELLOW;
		default: return null;
		}
	}
	
	private static BITeam[] teams = new BITeam[] { BLUE, RED, GREEN, YELLOW };
	public static BITeam[] getTeams() {
		return teams;
	}
}
