package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import db.Database;

public class Players {
	private Player[] players;

	public Players(int size) {
		players = new Player[size];
	}

	public Set<Player> getClones() {
		Set<Player> clones = new HashSet<Player>();
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null) {
				clones.add(players[i].publicClone(false));
			}
		}
		return clones;
	}

	public void add(Player player, int seat) {
		int adjustedSeat = seat % players.length;
		if (players[adjustedSeat] != null) {
			System.out.println("Seat " + seat + " is taken.");
			return;
		}

		players[adjustedSeat] = player;
		player.setSeat(adjustedSeat);
		;
	}

	public void remove(Player player) {
		if (player == null) {
			System.out.println("Cannot remove null.");
			return;
		}
		for (int i = 0; i < players.length; i++) {
			if (player.equals(players[i])) {
				players[i] = null;
				player.setSeat(-1);
			}
		}
	}

	public Player get(int index) {
		return players[index % players.length];
	}

	public int indexOf(Player player) {
		if (player == null) {
			System.out.println("Null has no index.");
			return -1;
		}
		for (int i = 0; i < players.length; i++) {
			if (player.equals(players[i])) {
				return -1;
			}
		}

		System.out.println("Player is not on list.");
		return -1;
	}

	public Player after(int index) {

		for (int j = 1; j <= players.length; j++) {
			int i = (index + j) % players.length;
			if (players[i] != null) {
				return players[i];
			}
		}

		System.out.println("No players left.");
		return null;
	}

	public int count() {
		int pCount = 0;
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null) {
				pCount++;
			}
		}
		return pCount;
	}

	public int countPlayersWithCards() {
		int pCount = 0;
		for (Player p : players) {
			if (p != null) {
				if (p.hasCards()) {
					pCount++;
				}
			}
		}
		return pCount;
	}

	public Set<Player> getPlayersWithCards() {
		Set<Player> pWithCards = new HashSet<Player>();
		for (Player p : players) {
			if (p != null) {
				if (p.hasCards()) {
					pWithCards.add(p);
				}
			}
		}
		return pWithCards;
	}

	public int getReadyPlayersCount() {
		int pCount = 0;
		for (int i = 0; i < players.length; i++) {

			if (players[i] != null) {
				if (players[i].getCash() > 0 && !players[i].isAway()) {
					pCount++;
				}
			}
		}
		return pCount;
	}

	public List<Player> getReadyPlayers(Player dealer) {
		List<Player> readies = new ArrayList<Player>();
		int index = indexOf(dealer);
		for (int i = 1; i <= players.length; i++) {
			int j = (i + index) % players.length;
			if (players[j] != null) {
				if (players[j].getCash() > 0 && !players[j].isAway()) {
					readies.add(players[j]);
				}
			}
		}
		return readies;
	}

	public void reset() {
		Map<String, Double> buyins = new HashMap<>();
		for (Player p : players) {
			if (p != null) {
				p.setCards(null);
				p.resetBet();
				p.resetWin();
				buyins.put(p.getName(), p.getCash());
			}
		}
		Database.massUpdateBuyin(buyins);

	}

	public void resetWins() {
		for (Player p : players) {
			if (p != null) {
				p.resetWin();
			}
		}

	}
}
