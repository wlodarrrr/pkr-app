package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cards.Card;

public class Evaluator {

	public static List<Player> winnersFrom(Set<Player> fighters, Card[] board) {

		if (fighters.size() == 0) {
			throw new IllegalStateException("Everyone folded.");
		}

		// find winners
		List<Player> winners = new ArrayList<>();
		for (Player p : fighters) {
			if (winners.size() == 0) {
				winners.add(p);
			} else {
				int result = compare(p.getCards(), winners.get(0).getCards(), board);
				if (result == 1) {
					winners.clear();
					winners.add(p);
				} else if (result == 0) {
					winners.add(p);
				}
			}
		}
		return winners;
	}

	final static String[] values = new String[] { "High Card", "Pair", "Two Pairs", "Set", "Straight", "Flush",
			"Full house", "Quads", "Poker" };

	public static int compare(Card[] hand, Card[] topHand) {
		final int[] ev1 = Evaluator.evaluate(hand);
		final int[] ev2 = Evaluator.evaluate(topHand);

		for (int i = 0; i < 6; i++) {
			if (ev1[i] > ev2[i]) {
				return 1;
			} else if (ev1[i] < ev2[i]) {
				return -1;
			}
		}
		return 0;
	}

	public static int compare(Card[] hand1, Card[] hand2, Card[] community) {
		final Card[] topHand1 = Evaluator.holdemEvaluate(hand1, community);
		final Card[] topHand2 = Evaluator.holdemEvaluate(hand2, community);
		final int[] ev1 = Evaluator.evaluate(topHand1);
		final int[] ev2 = Evaluator.evaluate(topHand2);

		for (int i = 0; i < 6; i++) {
			if (ev1[i] > ev2[i]) {
				return 1;
			} else if (ev1[i] < ev2[i]) {
				return -1;
			}
		}
		return 0;
	}

	public static String display(Card[] hand) {
		final int[] evaluation = Evaluator.evaluate(hand);
		return Evaluator.values[evaluation[0]] + " with " + hand[0] + ", " + hand[1] + ", " + hand[2] + ", " + hand[3]
				+ ", " + hand[4];
	}

	public static int[] evaluate(Card[] hand) {
		final int[] value = new int[] { 0, 0, 0, 0, 0, 0 }; // value + card ranks

		final int[] ranks = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; // 13 for all card ranks

		for (final Card c : hand) {
			ranks[c.getRank()]++;
		}

		// check for quads
		int index = 1;
		for (int j = 12; j >= 0; j--) {
			if (ranks[j] == 4) {
				for (int k = 0; k < 4; k++) {
					value[index] = j;
					index++;
				}
				value[0] = 7;
			}
		}

		// check for trips
		for (int j = 12; j >= 0; j--) {
			if (ranks[j] == 3) {
				for (int k = 0; k < 3; k++) {
					value[index] = j;
					index++;
				}
				value[0] = 3;
			}
		}

		// check for pairs
		for (int j = 12; j >= 0; j--) {
			if (ranks[j] == 2) {
				for (int k = 0; k < 2; k++) {
					value[index] = j;
					index++;
				}

				if (value[0] == 3) {
					value[0] = 6;
				} else if (value[0] == 1) {
					value[0] = 2;
				} else {
					value[0] = 1;
				}

			}
		}

		for (int j = 12; j >= 0; j--) {
			if (ranks[j] == 1) {
				value[index] = j;
				index++;
			}
		}

		// check for straight
		for (int x = 4; x < 13; x++) {
			if (ranks[x] == 1 && ranks[x - 1] == 1 && ranks[x - 2] == 1 && ranks[x - 3] == 1 && ranks[x - 4] == 1) {
				value[0] = 4;
				break;
			}
		}

		if (ranks[0] == 1 && ranks[1] == 1 && ranks[2] == 1 && ranks[3] == 1 && ranks[12] == 1) // ace high
		{
			value[0] = 4;
		}

		boolean flush = true; // assume there is a flush
		final int suit = hand[0].getSuit();
		for (final Card c : hand) {
			if (c.getSuit() != suit) {
				flush = false;
				break;
			}
		}

		if (flush) {
			if (value[0] == 4) {
				value[0] = 8;
			} else {
				value[0] = 5;
			}
		}

		return value;
	}

	public static Card[] holdemEvaluate(Card[] holeCards, Card[] board) {
		final Card[] cards = new Card[] { holeCards[0], holeCards[1], board[0], board[1], board[2], board[3],
				board[4] };
		final Card[] hand = new Card[5];
		final Card[] topHand = new Card[5];
		for (int i = 0; i < 5; i++) {
			topHand[i] = cards[i];
		}

		for (int i = 0; i < 3; i++) {
			for (int j = i + 1; j < 4; j++) {
				for (int k = j + 1; k < 5; k++) {
					for (int l = k + 1; l < 6; l++) {
						for (int m = l + 1; m < 7; m++) {
							hand[0] = cards[i];
							hand[1] = cards[j];
							hand[2] = cards[k];
							hand[3] = cards[l];
							hand[4] = cards[m];
							if (Evaluator.compare(hand, topHand) == 1) {
								for (int n = 0; n < 5; n++) {
									topHand[n] = hand[n];
								}
							}
						}
					}
				}
			}
		}

		return topHand;
	}
}
