package dev.turra.codenames.server.game;

import dev.turra.codenames.common.Role;
import dev.turra.codenames.common.Team;
import dev.turra.codenames.common.network.Packet;
import dev.turra.codenames.common.network.cb.PacketClientChat;
import dev.turra.codenames.common.network.cb.PacketClientUpdatePlayers;
import dev.turra.codenames.common.network.sb.PacketServerChat;
import dev.turra.codenames.common.network.sb.PacketServerLogin;
import dev.turra.codenames.common.network.sb.PacketServerTeamRole;
import dev.turra.codenames.server.Connection;
import dev.turra.codenames.server.IPacketListener;

import java.util.HashMap;
import java.util.stream.Collectors;

public class GameManager implements IPacketListener {

	HashMap<Integer, Player> players = new HashMap<>();

	public GameManager() {

	}

	@Override
	public void received(Packet packet, Connection connection) {
		if (packet instanceof PacketServerChat p) {
			System.out.println(players.get(connection.id).name + ": " + p.message);
			sendToAll(new PacketClientChat(players.get(connection.id).name, p.message));
		} else if (packet instanceof PacketServerLogin p) {
			players.put(connection.id, new Player(connection, p.name));
		} else if (packet instanceof PacketServerTeamRole p){
			switchTeam(connection.id, p.team, p.role);
		}
	}

	public void switchTeam(int id, Team team, Role role){
		Player player = players.get(id);
		Team oldTeam = player.team;
		Role oldRole = player.role;

		player.team = team;
		player.role = role;

		// Get a list of players in a Team and Role in form of a String.
		String players = this.players.values().stream().filter(p -> p.team == team).filter(p -> p.role == role).map(p -> p.name).collect(Collectors.joining(", "));

		PacketClientUpdatePlayers packet = new PacketClientUpdatePlayers(team, role, players);
		sendToAll(packet);

		// If the player was in a different team, remove that player from the old team
		if (oldTeam != null && oldRole != null) {
			String updateOld = this.players.values().stream().filter(p -> p.team == oldTeam).filter(p -> p.role == oldRole).map(p -> p.name).collect(Collectors.joining(", "));
			PacketClientUpdatePlayers updateOldPacket = new PacketClientUpdatePlayers(oldTeam, oldRole, updateOld);
			sendToAll(updateOldPacket);
		}
	}

	public void playerQuit(int id){
		players.remove(id);
	}

	public void sendToAll(Packet packet){
		for(Player player : players.values()){
			player.connection.sendPacket(packet);
		}
	}

	public enum State {
		LOBBY,
		INGAME
	}

}
