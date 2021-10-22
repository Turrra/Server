package dev.turra.codenames.server;

import dev.turra.codenames.common.network.Packet;
import dev.turra.codenames.common.network.sb.PacketServerChat;
import dev.turra.codenames.common.network.cb.PacketClientChat;

public class PacketListener {

	public void received(Packet p, Connection connection) {
		if (p instanceof PacketServerChat) {
			PacketServerChat packet = (PacketServerChat) p;
			System.out.println(connection.id + ": " + packet.message);
			ConnectionHandler.sendAll(new PacketClientChat(connection.id, packet.message));
		}
	}

}