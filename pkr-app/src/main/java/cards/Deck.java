package cards;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {

	private final ArrayList<Card> cards;

	public Deck() {
		cards = new ArrayList<>();
		for (int i = 0; i < Card.ranks.length; i++) {
			for (int j = 0; j < Card.suits.length; j++) {
				cards.add(new Card(i, j));
			}
		}

		Collections.shuffle(cards);
	}

	public Card deal() {
		return cards.remove(0);
	}
}
