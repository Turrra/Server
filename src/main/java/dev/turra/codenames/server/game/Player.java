package dev.turra.codenames.server.game;

import dev.turra.codenames.common.Role;
import dev.turra.codenames.common.Team;
import dev.turra.codenames.server.Connection;

public class Player {

	Connection connection;
	String name;

	public Team team;
	public Role role;

	public Player(Connection connection, String name) {
		this.connection = connection;
		this.name = name;
	}

	public Connection getConnection() {
		return connection;
	}

	public String getName() {
		return name;
	}
}
