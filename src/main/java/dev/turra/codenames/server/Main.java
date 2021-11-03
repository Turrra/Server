package dev.turra.codenames.server;

import dev.turra.codenames.server.game.GameManager;

public class Main {

	public static GameManager manager;

	public static void main(String[] args){
		Server server = new Server(5252);
		server.start();

		manager = new GameManager();
	}
}
