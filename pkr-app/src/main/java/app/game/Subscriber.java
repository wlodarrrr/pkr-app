package app.game;

import java.util.Set;

import app.utils.Card;

public interface Subscriber {

	String getName();

	void updatePlayer(Player p);

	void updateBoard(Card[] boardToShow);

	void updatePot(double totalPot);

	void updateDealer(int dealerPosition);

	void updateHoleCards(Card[] cards);

	void updateBankroll(double amount);

	void toAct(Player publicClone, double toCall, double totalPot);

	void removePlayer(Player player);

	void doShowdown(Set<Player> playersToShow, Card[] clone);

	void updateTimer(int timerCounter);

	void acceptSeat(int index);
}
