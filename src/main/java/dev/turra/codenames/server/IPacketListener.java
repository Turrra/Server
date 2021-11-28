package dev.turra.codenames.server;

import dev.turra.codenames.common.network.Packet;

public interface IPacketListener {

	void received(Packet p, Connection connection);

}