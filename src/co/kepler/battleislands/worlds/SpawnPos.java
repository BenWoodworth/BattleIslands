package co.kepler.battleislands.worlds;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

public class SpawnPos {
	double x, y, z;
	float yaw, pitch;
	
	public SpawnPos(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public SpawnPos(double x, double y, double z, double yaw, double pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = (float)yaw;
		this.pitch = (float)pitch;
	}
	
	public SpawnPos(List<Double> vals) {
		if (vals == null)
			throw new IllegalArgumentException("List cannot be null");
		if (vals.size() != 5)
			throw new IllegalArgumentException("List size must equal 5");
		x = vals.get(0);
		y = vals.get(1);
		z = vals.get(2);
		yaw = vals.get(3).floatValue();
		pitch = vals.get(4).floatValue();
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getZ() {
		return z;
	}
	public float getYaw() {
		return yaw;
	}
	public float getPitch() {
		return pitch;
	}
	
	public Location getLocation(World w) {
		return new Location(w, x, y, z, yaw, pitch);
	}
}
