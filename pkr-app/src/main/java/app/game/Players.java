package app.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.db.Database;

public class Players {
	private Player[] players;
	private int actorPos;
	private int dealerPos;
	private Database db;
	private int size;

	public Players(Database db, int size) {
		this.db = db;
		this.size = size;
		players = new Player[size];
		actorPos = -1;
		dealerPos = -1;
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
		if (players[seat] != null) {
			return;
		}
		players[seat] = player;
		player.setSeat(seat);
	}

	public void remove(Player player) {
		if (player == null) {
			return;
		}
		for (int i = 0; i < players.length; i++) {
			if (player.equals(players[i])) {
				players[i] = null;
			}
		}
	}

	public int indexOf(Player player) {
		if (player == null) {
			return -1;
		}
		for (int i = 0; i < players.length; i++) {
			if (player.equals(players[i])) {
				return i;
			}
		}
		return -1;
	}

	public int nextDealer() {

		for (int j = 1; j <= players.length; j++) {
			int i = (dealerPos + j) % players.length;
			if (players[i] != null) {
				actorPos = i;
				dealerPos = i;
				return i;
			}
		}
		actorPos = -1;
		dealerPos = -1;
		return -1;
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

	public int countReadyPlayers() {
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

	public int countPlayersWithCardsAndCash() {
		int pCount = 0;
		for (Player p : players) {
			if (p != null) {
				if (p.hasCards() && p.getCash() > 0) {
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

	public List<Player> getReadyPlayers() {
		List<Player> readies = new ArrayList<Player>();
		for (Player p : players) {
			if (p != null) {
				if (p.isReady()) {
					readies.add(p);
				}
			}
		}
		return readies;
	}

	public void resetAll() {
		Map<String, Double> buyins = new HashMap<>();
		for (Player p : players) {
			if (p != null) {
				p.setCards(null);
				p.resetBet();
				buyins.put(p.getName(), p.getCash());
			}
		}
		db.massUpdateBuyin(buyins);

	}

	public void resetBets() {
		for (Player p : players) {
			if (p != null) {
				p.resetBet();
			}
		}
	}

	public Player currentActor() {
		return players[actorPos];
	}

	public Player previousActor() {
		int counter = 0;
		int index = actorPos;
		while (counter < size) {
			index = (index - 1) % players.length;
			Player p = players[index];
			if (p != null) {
				if (p.canPlay()) {
					actorPos = index;
					return p;
				}
			}
			counter++;
		}
		actorPos = -1;
		return null;
	}

	public Player nextActor() {
		int counter = 0;
		int index = actorPos;
		while (counter < size) {
			index = (index + 1) % players.length;
			Player p = players[index];
			if (p != null) {
				if (p.canPlay()) {
					actorPos = index;
					return p;
				}
			}
			counter++;
		}
		actorPos = -1;
		return null;
	}

	public void resetActor() {
		actorPos = dealerPos;
		nextActor();
	}

	public Player get(int seat) {
		return players[seat];
	}

	public int indexOf(String name) {
		for (int i = 0; i < size; i++) {
			if (players[i] != null) {
				if (players[i].getName().contentEquals(name)) {
					return i;
				}
			}
		}
		return -1;
	}
}
