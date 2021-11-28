package dev.turra.codenames.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Represents the server of the entire game
 */
public class Server implements Runnable{

	private int port;
	private ServerSocket serverSocket;
	private boolean running = false;
	private int id = 0;

	/**
	 * Creates a server with a port
	 * @param port
	 */
	public Server(int port) {
		this.port = port;

		try {
			serverSocket = new ServerSocket(port);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the server thread
	 */
	public void start() {
		new Thread(this).start();
	}

	/**
	 * Allows new connections to the server
	 */
	@Override
	public void run() {
		running = true;
		System.out.println("Server started on port: " + port);

		while(running) {
			try {
				Socket socket = serverSocket.accept();
				initSocket(socket);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		shutdown();
	}

	/**
	 * Initializes a new connection with a socket
	 * @param socket
	 */
	private void initSocket(Socket socket) {
		Connection connection = new Connection(socket,id);
//		ConnectionHandler.connections.put(id,connection);
		new Thread(connection).start();
		id++;
	}

	/**
	 * Shuts down the server
	 */
	public void shutdown() {
		running = false;

		try {
			serverSocket.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}
