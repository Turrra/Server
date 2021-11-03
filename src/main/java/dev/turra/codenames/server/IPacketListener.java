package dev.turra.codenames.server;

import dev.turra.codenames.common.network.Packet;
import dev.turra.codenames.common.network.sb.PacketServerChat;
import dev.turra.codenames.common.network.cb.PacketClientChat;

public interface IPacketListener {

	void received(Packet p, Connection connection);

}