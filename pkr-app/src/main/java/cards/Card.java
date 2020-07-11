package cards;

import com.vaadin.flow.component.html.Image;

public class Card {
	public static final String[] ranks = new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "10", "Walet", "Dama",
			"Król", "As" };

	public static final String[] shortRanks = new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K",
			"A" };
	public static final String[] shortSuits = new String[] { "D", "H", "C", "S" };
	public static final String[] suits = new String[] { "Karo", "Kier", "Trefl", "Pik" };

	public static Image getFaceDownCard() {
		return new Image("images/facedown.png", "");
	}

	private final int rank;

	private final int suit;

	public Card(int rank, int suit) {
		if (0 > rank || rank >= Card.ranks.length) {
			throw new IllegalStateException("Invalid rank");
		}
		if (0 > suit || suit >= Card.suits.length) {
			throw new IllegalStateException("Invalid suit");
		}
		this.suit = suit;
		this.rank = rank;

	}

	public Card() {
		suit = -1;
		rank = -1;
	}

	public int compareTo(Card card) {
		if (hashCode() > card.hashCode()) {
			return 1;
		} else if (hashCode() == card.hashCode()) {
			return 0;
		} else {
			return -1;
		}
	}

	public Image getImage() {
		return new Image("images/" + toShortString() + ".png", "");
	}

	public int getRank() {
		return rank;
	}

	public int getSuit() {
		return suit;
	}

	@Override
	public int hashCode() {
		return rank * Card.suits.length + suit;
	}

	public String toShortString() {
		if (suit == -1) {
			return "facedown";
		} else {
			return Card.shortRanks[rank] + Card.shortSuits[suit];
		}
	}

	@Override
	public String toString() {
		if (suit == -1) {
			return "ukryta";
		} else {
			return Card.ranks[rank] + " " + Card.suits[suit];
		}
	}
}
