package app.game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import app.chat.Chatter;
import app.chat.Message;
import app.db.Database;
import app.utils.Card;
import app.utils.Deck;
import app.utils.TextConstants;

@Service
public class Game {

	private static final int size = 10;
	private final Set<Subscriber> subscribers = new HashSet<Subscriber>();
	private final Subscriber[] sittingSubs = new Subscriber[size];
	private final Players players;
	private boolean running = false;
	private double blind = 1;
	private int dealerPosition = -1;
	private Card[] board = new Card[5];
	private Set<Pot> pots = new HashSet<Pot>();
	private double bet = 0;
	private int playersToAct = 0;
	private int round = 0;
	private Database db;

	public Game(@Autowired Database db) {
		this.db = db;
		players = new Players(db, size);
	}

	private void tryStart() {
		if (running) {
			return;
		}

		if (players.countReadyPlayers() < 2) {
			return;
		}

		running = true;
		dealerPosition = players.nextDealer();

		dealCards(players.getReadyPlayers());

		// blinds
		final Player sb = players.nextActor();
		addToPot(sb, sb.pay(blind / 2));

		final Player bb = players.nextActor();
		addToPot(bb, bb.pay(blind));

		bet = blind;
		playersToAct = players.countPlayersWithCardsAndCash();

		for (Subscriber s : subscribers) {
			s.updateDealer(dealerPosition);
			fullRefresh(s);
		}

	}

	private void dealCards(List<Player> list) {
		final Deck deck = new Deck();
		for (final Player p : list) {
			p.setCards(new Card[] { deck.deal(), deck.deal() });
		}

		board = new Card[5];
		for (int i = 0; i < 5; i++) {
			board[i] = deck.deal();
		}
	}

	private void addToPot(Player player, double amount) {
		for (final Pot pot : pots) {
			if (!pot.contains(player)) {
				final double potBet = pot.getBet();
				if (amount >= potBet) {
					// Regular call, bet or raise.
					pot.add(player);
					amount -= pot.getBet();
				} else {
					// Partial call (all-in); redistribute pots.
					pots.add(pot.split(player, amount));
					amount = 0;
				}
			}
			if (amount <= 0) {
				break;
			}
		}
		if (amount > 0) {
			final Pot pot = new Pot(amount);
			pot.add(player);
			pots.add(pot);
		}
	}

	private void endRound() {
		// reset
		players.resetBets();
		players.resetActor();
		bet = 0;

		// count players left
		playersToAct = players.countPlayersWithCardsAndCash();
		if (playersToAct < 2) {
			endHand();
			return;
		}

		// check if it was last round
		round++;
		if (round == 4) {
			endHand();
			return;
		}

		for (Subscriber s : subscribers) {
			fullRefresh(s);
		}
	}

	private void endHand() {

		Set<Player> clonesToShow = new HashSet<>();

		// assign winnings to players
		players.resetBets();

		Set<Player> playersWithCards = players.getPlayersWithCards();
		if (playersWithCards.size() == 1) {
			playersWithCards.forEach(p -> {
				p.win(totalPot());
				Player clone = p.publicClone(false);
				clonesToShow.add(clone);
			});
		} else {
			round = 4;
			Set<Player> playersToShow = new HashSet<>();
			for (Pot pot : pots) {
				Set<Player> fighters = pot.contributors();
				fighters.retainAll(playersWithCards);
				List<Player> winners = Evaluator.winnersFrom(fighters, board);
				for (Player p : winners) {
					p.win(pot.size() / winners.size());
					playersToShow.add(p);
				}
			}
			for (Player p : playersToShow) {
				clonesToShow.add(p.publicClone(true));
			}
		}

		// showdown
		final Card[] boardToShow = round == 0 ? null : Arrays.copyOf(board, Math.min(round + 2, 5));
		for (Subscriber s : subscribers) {
			s.doShowdown(clonesToShow, boardToShow);
		}
		// construct text message
		String boardToString = TextConstants.BOARD + ": " + cardsToString(boardToShow);
		Chatter.send(new Message("", TextConstants.HAND_ENDED));
		Chatter.send(new Message("", boardToString));
		for (Player p : clonesToShow) {
			String pToShow = p.getName() + " " + cardsToString(p.getCards()) + " " + TextConstants.WON + " "
					+ p.getBet() + ".";
			Chatter.send(new Message("", pToShow));
		}

		// cleanup
		players.resetAll();
		for (int i = 0; i < 5; i++) {
			board[i] = null;
		}
		pots.clear();
		bet = 0;
		playersToAct = 0;
		round = 0;

		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				running = false;
				tryStart();
				if (!running) {
					for (Subscriber s : subscribers) {
						fullRefresh(s);
					}

				}

			}

		}, 4444);

	}

	private String cardsToString(Card[] cards) {
		if (cards == null) {
			return "[]";
		}
		if (cards.length == 0) {
			return "[]";
		}
		String s = "[";
		for (Card c : cards) {
			s += c.toString() + ",";
		}
		s = s.substring(0, s.length() - 1) + "]";
		return s;
	}

	private void fullRefresh(Subscriber subscriber) {
		final Set<Player> clones = players.getClones();
		for (Player p : clones) {
			subscriber.updatePlayer(p);
		}
		if (running) {
			final Card[] boardToShow = round == 0 ? null : Arrays.copyOf(board, Math.min(round + 2, 5));
			subscriber.updateBoard(boardToShow);
			subscriber.updatePot(totalPot());
			subscriber.updateDealer(dealerPosition);

			final int i = players.indexOf(subscriber.getName());
			if (i != -1) {
				final Card[] cards = players.get(i).getCards();
				subscriber.updateHoleCards(cards);
			}

			final Player actor = players.currentActor();
			if (actor != null) {
				subscriber.toAct(actor.publicClone(false), bet - actor.getBet(), totalPot());
			}

		} else {
			subscriber.updateBoard(null);
			subscriber.updatePot(0);
			subscriber.updateHoleCards(null);
		}
	}

	private double totalPot() {
		double sum = 0;
		for (Pot pot : pots) {
			sum += pot.size();
		}
		return sum;
	}

	public void act(Subscriber subscriber, Action action, double amount) {
		int index = players.indexOf(subscriber.getName());
		if (index == -1) {
			return;
		}
		final Player player = players.get(index);
		if (!player.equals(players.currentActor())) {
			return;
		}

		// round to all in if amount is close or bigger
		if (player.getCash() - amount < 0.01) {
			amount = player.getCash();
		}

		double toCall = bet - player.getBet();
		switch (action) {
		case FOLD:
			player.resetBet();
			player.setCards(null);
			db.updateBuyin(player.getName(), player.getCash());

			playersToAct--;
			break;
		case CALL:
			addToPot(player, player.pay(toCall));
			playersToAct--;
			break;
		case RAISE:
			if (toCall >= amount) {
				return;
			}

			addToPot(player, player.pay(amount));
			bet = player.getBet();

			playersToAct = players.countPlayersWithCardsAndCash();
			if (!player.isAllin()) {
				playersToAct--;
			}
			break;
		}
		Player pClone = player.publicClone(false);
		for (Subscriber s : subscribers) {
			s.updatePlayer(pClone);
		}

		// if there is none to act or after fold there is only one person with cards
		if (playersToAct < 1 || (action == Action.FOLD && players.countPlayersWithCards() == 1)) {
			endRound();
		} else {

			final Player actor = players.nextActor();
			final Player clone = actor.publicClone(false);
			for (Subscriber s : subscribers) {
				s.toAct(clone, bet - clone.getBet(), totalPot());
			}
		}

	}

	public boolean join(Subscriber subscriber) {
		int index = players.indexOf(subscriber.getName());

		if (index != -1) {

			// handle case where player is already created for this subscriber
			if (sittingSubs[index] != null) {
				subscribers.remove(sittingSubs[index]);
			}
			sittingSubs[index] = subscriber;
		} else {

			// handle case where subscriber is not really in game, but has buyin
			db.buyout(subscriber.getName());
		}
		subscribers.add(subscriber);
		fullRefresh(subscriber);
		return true;
	}

	public boolean sit(Subscriber subscriber, int seat, double buyin) {
		int index = players.indexOf(subscriber.getName());
		if (index != -1) {
			return false;
		}
		if (players.get(seat) != null) {
			return false;
		}

		final Player p = new Player(subscriber.getName(), buyin);
		db.buyin(subscriber.getName(), buyin);
		players.add(p, seat);
		final Player clone = p.publicClone(false);
		sittingSubs[seat] = subscriber;
		for (Subscriber s : subscribers) {
			s.updatePlayer(clone);
		}
		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				tryStart();
			}

		}, 555);
		return true;
	}

	public void stand(Subscriber subscriber) {
		int index = players.indexOf(subscriber.getName());
		if (index == -1) {
			return;
		}

		Player player = players.get(index);
		if (player.hasCards()) {
			return;
		}

		sittingSubs[index] = null;
		db.buyout(player.getName());
		final Player clone = player.publicClone(false);
		for (Subscriber s : subscribers) {
			s.removePlayer(clone);
		}
		players.remove(player);

	}

	public boolean setAway(Subscriber subscriber, boolean away) {
		int index = players.indexOf(subscriber.getName());
		if (index != -1) {
			Player player = players.get(index);
			player.setAway(away);
			Player clone = player.publicClone(false);
			if (!player.hasCards()) {
				for (Subscriber s : subscribers) {
					s.updatePlayer(clone);
				}
			}

			if (!away) {
				tryStart();
			}
			return true;

		}
		return false;
	}
}
