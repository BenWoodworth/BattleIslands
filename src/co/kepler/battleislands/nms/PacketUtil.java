package co.kepler.battleislands.nms;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutExperience;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketUtil {
	
	public static void showTitle(String titleJson, String subtitleJson,
			int in, int stay, int out, Player... players) {
		IChatBaseComponent cTitle = ChatSerializer.a(titleJson);
		IChatBaseComponent cSubtitle = ChatSerializer.a(subtitleJson);

		PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(
				EnumTitleAction.TITLE, cTitle);
		PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(
				EnumTitleAction.SUBTITLE, cSubtitle);
		PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(
				EnumTitleAction.TIMES, null, in, stay, out);
		
		PlayerConnection connection;
		for (Player p : players) {
			connection = ((CraftPlayer)p).getHandle().playerConnection;
			connection.sendPacket(titlePacket);
			connection.sendPacket(subtitlePacket);
			connection.sendPacket(timesPacket);
		}
	}
	
	public static void sendActionBarMessage(String text, Player... players) {
		IChatBaseComponent cText = ChatSerializer.a(text);
		PacketPlayOutChat packet = new PacketPlayOutChat(cText, (byte)2);
		PlayerConnection connection;
		for (Player p : players) {
			connection = ((CraftPlayer)p).getHandle().playerConnection;
			connection.sendPacket(packet);
		}
	}
	
	public static void setExp(float exp, int level, Player... players) {
		PacketPlayOutExperience packet = new PacketPlayOutExperience(exp, 0, level);
		PlayerConnection connection;
		for (Player p : players) {
			connection = ((CraftPlayer)p).getHandle().playerConnection;
			connection.sendPacket(packet);
		}
	}
}
