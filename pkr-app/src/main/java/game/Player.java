package game;

import cards.Card;

public class Player {

	private String name;
	private double cash;
	private int seat;
	private double bet;
	private double win;
	private boolean isAway;
	private boolean hasCards;
	private Card[] cards;

	public Player(String name, double cash, int seat) {
		this.name = name;
		this.cash = cash;
		this.seat = seat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCash() {
		return cash;
	}

	public double pay(double amount) {
		if (amount < 0) {
			System.out.println("Negative payment.");
			return 0;
		} else {
			double payment = Math.min(cash, amount);
			cash -= payment;
			bet += payment;
			return payment;
		}
	}

	public void win(double amount) {
		if (amount < 0) {
			System.out.println("player can't win negative amount.");
			return;
		} else {
			cash += amount;
			win += amount;
		}
	}

	public double getWin() {
		return win;
	}

	public void resetWin() {
		win = 0;

	}

	public double getBet() {
		return bet;
	}

	public void resetBet() {
		bet = 0;
	}

	public boolean isAway() {
		return isAway;
	}

	public void setAway(boolean isAway) {
		this.isAway = isAway;
	}

	public boolean hasCards() {
		return hasCards;
	}

	public Card[] getCards() {
		return cards;
	}

	public void setCards(Card[] cards) {
		this.cards = cards;
		this.hasCards = cards != null;
	}

	public int getSeat() {
		return seat;
	}

	public void setSeat(int seat) {
		this.seat = seat;
	}

	public boolean isAllin() {
		return hasCards && cash == 0;
	}

	public Player publicClone(boolean showCards) {
		Player clone = new Player(name, cash, seat);
		clone.bet = bet;
		clone.win = win;
		clone.isAway = isAway;
		clone.hasCards = hasCards;
		if (showCards) {
			clone.cards = cards;
		}

		return clone;
	}
}
