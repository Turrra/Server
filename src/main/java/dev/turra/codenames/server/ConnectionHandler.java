package dev.turra.codenames.server;

import dev.turra.codenames.common.network.Packet;

import java.util.HashMap;

public class ConnectionHandler {

	public static HashMap<Integer,Connection> connections = new HashMap<Integer,Connection>();

	public static void sendAll(Packet packet){
		for(Connection c : connections.values()){
			c.sendObject(packet);
		}
	}

}