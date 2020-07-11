package game;

import java.util.List;

public class Actors {

	private List<Player> actors;
	private Player actor;

	public Actors(List<Player> list) {
		if (list == null) {
			System.out.println("Null list.");
			return;
		}
		if (list.size() == 0) {
			System.out.println("Empty list.");
			return;
		}

		actors = list;
		actor = list.get(0);
	}

	public int size() {
		return actors.size();
	}

	public Player currentActor() {
		return actor;
	}

	public Player previousActor() {
		int i = actors.indexOf(actor);
		i--;
		while (size() > 0) {
			Player p = get(i);
			if (p.isAllin()) {
				actors.remove(i);
				i--;
			} else {
				actor = get(i);
				return actor;
			}
		}
		return null;
	}

	public Player nextActor() {
		int i = actors.indexOf(actor);
		i++;
		while (size() > 0) {
			Player p = get(i);
			if (p.isAllin()) {
				actors.remove(i);
			} else {
				actor = get(i);
				return actor;
			}
		}
		return null;
	}

	public void remove(Player player) {
		if (actor.equals(player)) {
			previousActor();
		}
		actors.remove(player);
	}

	public Player get(int index) {
		int adjustedIndex = index % actors.size();
		while (adjustedIndex < 0) {
			adjustedIndex += actors.size();
		}
		return actors.get(adjustedIndex);
	}

	public Player reset() {
		for (Player a : actors) {
			a.resetBet();
		}
		for (int i = actors.size() - 1; i >= 0; i--) {
			if (actors.get(i).isAllin()) {
				actors.remove(i);
			}
		}
		if (actors.size() == 0) {
			return null;
		} else {
			actor = get(-1);
			nextActor();
			return actor;
		}
	}
}
