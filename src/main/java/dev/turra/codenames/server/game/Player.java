package dev.turra.codenames.server.game;

import dev.turra.codenames.common.Role;
import dev.turra.codenames.common.Team;
import dev.turra.codenames.server.Connection;

/**
 * Represents a player in the game.
 */
public class Player {

	Connection connection;
	String name;

	public Team team;
	public Role role;

	/**
	 * @param connection Connection to the player.
	 * @param name Name of the player.
	 */
	public Player(Connection connection, String name) {
		this.connection = connection;
		this.name = name;
	}

	/**
	 *
	 * @return Connection to the player.
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 *
	 * @return Name of the player.
	 */
	public String getName() {
		return name;
	}
}
