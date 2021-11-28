package dev.turra.codenames.server;

import dev.turra.codenames.common.network.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Represents a connection to a client.
 */
public class Connection implements Runnable{

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public int id;
	private IPacketListener listener;
	private boolean running = false;

	/**
	 * Creates a new connection.
	 * @param socket The socket to use.
	 * @param id The ID of the player.
	 */
	public Connection(Socket socket, int id) {
		this.socket = socket;
		this.id = id;

		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			listener = Main.manager;
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Runs the connection.
	 */
	@Override
	public void run() {
		try {
			running = true;

			while(running) {
				try {
					Packet packet = (Packet) in.readObject();
					listener.received(packet, this);
				}catch(ClassNotFoundException e) {
					System.err.println("Could not find packet: " + e.getMessage());
				}
			}
		}catch(IOException e) {
			close();
			System.err.println("UserID " + id + " dropped connection: " + e.getMessage());
		}
	}

	/**
	 * Closes the connection.
	 */
	public void close() {
		try {
			running = false;
			in.close();
			out.close();
			socket.close();
			Main.manager.playerQuit(id);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a packet to the client.
	 * @param packet The packet to send.
	 */
	public void sendPacket(Packet packet) {
		if(socket.isClosed())
			return;

		try {
			out.writeObject(packet);
			out.flush();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

}