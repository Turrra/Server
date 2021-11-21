package dev.turra.codenames.server.game;

import dev.turra.codenames.common.Role;
import dev.turra.codenames.common.Team;
import dev.turra.codenames.common.network.Packet;
import dev.turra.codenames.common.network.cb.PacketClientCard;
import dev.turra.codenames.common.network.cb.PacketClientCardReveal;
import dev.turra.codenames.common.network.cb.PacketClientChat;
import dev.turra.codenames.common.network.cb.PacketClientUpdatePlayers;
import dev.turra.codenames.common.network.sb.PacketServerCardClick;
import dev.turra.codenames.common.network.sb.PacketServerChat;
import dev.turra.codenames.common.network.sb.PacketServerLogin;
import dev.turra.codenames.common.network.sb.PacketServerTeamRole;
import dev.turra.codenames.common.CardColor;
import dev.turra.codenames.server.Connection;
import dev.turra.codenames.server.IPacketListener;

import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GameManager implements IPacketListener {

	private List<String> words = new ArrayList<>();

	private HashMap<Integer, Player> players = new HashMap<>();
	private Card[][] board = new Card[5][5];

	private Team currentTurn;

	public GameManager() {

		// Load words from words.txt to words
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream("words.txt");
		Scanner scanner = new Scanner(inputStream);
		while (scanner.hasNextLine()) {
			words.add(scanner.nextLine());
		}
		scanner.close();

		generateBoard();
		// Print words with their colors
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				System.out.print(board[i][j].getColor().toString() + " " + board[i][j].getWord() + ", ");
			}
			System.out.println();
		}
	}

	public void switchTeam(int id, Team team, Role role) {
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

	public void generateBoard() {
		// Get 25 random unique words from the word bank
		List<String> selected = new Random().ints(0, words.size()).distinct().limit(25).mapToObj(words::get).collect(Collectors.toList());

		// set current turn to a random team
		currentTurn = Team.values()[new Random().nextInt(2)];

		// Create 25 colors
		List<CardColor> colors = new ArrayList<>();
		for (int i = 0; i < (currentTurn == Team.BLUE ? 9 : 8); i++) {
			colors.add(CardColor.BLUE);
		}
		for (int i = 0; i < (currentTurn == Team.RED ? 9 : 8); i++) {
			colors.add(CardColor.RED);
		}
		for (int i = 0; i < 7; i++) {
			colors.add(CardColor.CITIZEN);
		}
		colors.add(CardColor.ASSASSIN);

		// Shuffle words and colors
		Collections.shuffle(selected);
		Collections.shuffle(colors);

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				board[i][j] = new Card(i, j, selected.get(i * 5 + j), colors.get(i * 5 + j));
			}
		}
	}

	public void sendCardPackets(Player playerToSend) {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (playerToSend == null) {
					for (Player player : players.values()) {
						PacketClientCard packet = new PacketClientCard(i, j, board[i][j].getWord(), player.role == Role.SPYMASTER ? board[i][j].getColor() : null);
						player.connection.sendPacket(packet);
					}
				} else {
					PacketClientCard packet = new PacketClientCard(i, j, board[i][j].getWord(), playerToSend.role == Role.SPYMASTER ? board[i][j].getColor() : null);
					playerToSend.connection.sendPacket(packet);
				}
			}
		}

	}

	// Handle player leaving the game
	public void playerQuit(int id) {
		Team team = players.get(id).team;
		Role role = players.get(id).role;
		String updatedList = this.players.values().stream().filter(p -> p.team == team).filter(p -> p.role == role).map(p -> p.name).collect(Collectors.joining(", "));
		PacketClientUpdatePlayers updateOldPacket = new PacketClientUpdatePlayers(team, role, updatedList);
		sendToAll(updateOldPacket);

		players.remove(id);
	}

	public void sendToAll(Packet packet) {
		for (Player player : players.values()) {
			player.connection.sendPacket(packet);
		}
	}

	@Override
	public void received(Packet packet, Connection connection) {
		if (packet instanceof PacketServerChat p) {
			System.out.println(players.get(connection.id).name + ": " + p.getMessage());
			sendToAll(new PacketClientChat(players.get(connection.id).name, p.getMessage()));
		} else if (packet instanceof PacketServerLogin p) {
			players.put(connection.id, new Player(connection, p.getName()));
			sendCardPackets(players.get(connection.id));
		} else if (packet instanceof PacketServerTeamRole p) {
			switchTeam(connection.id, p.getTeam(), p.getRole());
			sendCardPackets(players.get(connection.id));
		} else if (packet instanceof PacketServerCardClick p) {
			clickCard(connection, p);
		}
	}

	private void clickCard(Connection connection, PacketServerCardClick p) {
		Card card = board[p.getX()][p.getY()];

		if(players.get(connection.id).role != Role.OPERATIVE)
			return;

		if(players.get(connection.id).team != currentTurn)
			return;

		if (card.isRevealed())
			return;

		PacketClientCardReveal revealPacket = new PacketClientCardReveal(p.getX(), p.getY(), card.getColor());
		sendToAll(revealPacket);

		card.setRevealed(true);
	}

	/**
	 * TODO:
	 * 1. Add a turn system. Make sure to optimise if there are multiple spymasters
	 * 2.
	 */

}
