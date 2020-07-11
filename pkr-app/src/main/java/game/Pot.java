package game;

import java.util.HashSet;
import java.util.Set;

public class Pot {

	private double bet;
	private final Set<Player> contributors;

	public Pot(double amount) {
		bet = amount;
		contributors = new HashSet<>();
	}

	public void add(Player player) {
		contributors.add(player);

	}

	public boolean contains(Player player) {
		return contributors.contains(player);
	}

	public double getBet() {
		return bet;
	}

	public double size() {
		return bet * contributors.size();
	}

	public Pot split(Player player, double amount) {
		if (bet <= amount) {
			System.out.println("Player can fill the pot");
			return null;
		}

		bet -= amount;

		final Pot pot = new Pot(amount);
		pot.add(player);
		for (final Player p : contributors) {
			pot.add(p);
		}

		return pot;
	}

	public Set<Player> contributors() {
		return contributors;

	}

}
