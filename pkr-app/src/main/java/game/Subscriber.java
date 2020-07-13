package game;

import java.util.Set;

import cards.Card;

public interface Subscriber {

	String getName();

	void updatePlayer(Player p);

	void updateBoard(Card[] boardToShow);

	void updatePot(double totalPot);

	void updateDealer(int dealerPosition);

	void updateHoleCards(Card[] cards);

	void toAct(Player publicClone, double toCall, double totalPot);

	void logout();

	void removePlayer(Player player);

	default void doAction(Action action, double amount) {
		Game.getInstance().act(this, action, amount);
	}

	default void sit(int seat, double buyin) {
		Game.getInstance().sit(this, seat, buyin);
	}
	
	default boolean setAway(boolean away) {
		return Game.getInstance().setAway(this, away);
	}

	default void stand() {
		Game.getInstance().stand(this);
	}

	default void join() {
		Game.getInstance().join(this);
	}

	void doShowdown(Set<Player> playersToShow, Card[] clone);
}
