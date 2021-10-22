package dev.turra.codenames.server;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		Server server = new Server(5252);
		server.start();
	}
}
