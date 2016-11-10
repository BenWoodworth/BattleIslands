package co.kepler.battleislands;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

public class PacketListener extends PacketAdapter {

	public static void register(Plugin p) {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener(p));
	}

	public PacketListener(Plugin plugin) {
		super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.RESPAWN);
	}

	@Override
	public void onPacketSending(PacketEvent e) {
		
	}
}
