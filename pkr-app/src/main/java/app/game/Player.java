package app.game;

import app.utils.Card;

public class Player {

	private String name;
	private double cash;
	private int seat;
	private double bet;
	private boolean away;
	private boolean hasCards;
	private Card[] cards;

	public Player(String name, double cash) {
		this.name = name;
		this.cash = cash;
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
			return;
		} else {
			cash += amount;
			bet += amount;
		}
	}

	public double getBet() {
		return bet;
	}

	public void resetBet() {
		bet = 0;
	}

	public boolean isAway() {
		return away;
	}

	public void setAway(boolean isAway) {
		this.away = isAway;
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
		Player clone = new Player(name, cash);
		clone.seat = seat;
		clone.bet = bet;
		clone.away = away;
		clone.hasCards = hasCards;
		if (showCards) {
			clone.cards = cards;
		}

		return clone;
	}

	public boolean canPlay() {
		return hasCards && cash > 0;
	}

	public boolean isReady() {
		return !away && cash > 0;
	}
}
