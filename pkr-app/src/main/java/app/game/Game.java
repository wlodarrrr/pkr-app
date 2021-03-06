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
import app.chat.Message.Type;
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
	private Timer timer;
	private int timerCounter = 0;

	public Game(@Autowired Database db) {
		this.db = db;
		players = new Players(db, size);
	}

	private synchronized void tryStart() {
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
		players.nextActor();

		for (Subscriber s : subscribers) {
			fullRefresh(s);
		}

	}

	private synchronized void dealCards(List<Player> list) {
		final Deck deck = new Deck();
		for (final Player p : list) {
			p.setCards(new Card[] { deck.deal(), deck.deal() });
		}

		board = new Card[5];
		for (int i = 0; i < 5; i++) {
			board[i] = deck.deal();
		}
	}

	private synchronized void addToPot(Player player, double amount) {
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

	private synchronized void endRound() {
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

	private synchronized void endHand() {

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

		// show down
		final Card[] boardToShow = round == 0 ? null : Arrays.copyOf(board, Math.min(round + 2, 5));
		for (Subscriber s : subscribers) {
			s.updateHoleCards(null);
			s.doShowdown(clonesToShow, boardToShow);
		}
		// construct text message
		showdownOnChat(clonesToShow, boardToShow);

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

	private synchronized void showdownOnChat(Set<Player> clonesToShow, final Card[] boardToShow) {
		Chatter.send(new Message("", TextConstants.HAND_ENDED, Type.SYSTEM));

		// send board cards
		if (boardToShow != null) {
			if (boardToShow.length > 0) {
				String boardToString = TextConstants.BOARD + ": " + cardsToString(boardToShow);
				Chatter.send(new Message("", boardToString, Type.SYSTEM));
			}
		}

		// send players with winnings + optionally cards
		for (Player p : clonesToShow) {
			String cardsToShow = cardsToString(p.getCards());
			String textToShow = p.getName() + " ";
			textToShow += cardsToShow.length() == 0 ? "" : "[" + cardsToShow + "] ";
			textToShow += TextConstants.WON + " " + p.getBet() + ".";
			Chatter.send(new Message("", textToShow, Type.SYSTEM));
		}
	}

	private synchronized String cardsToString(Card[] cards) {
		if (cards == null) {
			return "";
		}
		if (cards.length == 0) {
			return "";
		}
		String s = "[";
		for (Card c : cards) {
			s += c.toString() + ", ";
		}
		s = s.substring(0, s.length() - 2) + "]";
		return s;
	}

	private synchronized void fullRefresh(Subscriber subscriber) {
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

	private synchronized double totalPot() {
		double sum = 0;
		for (Pot pot : pots) {
			sum += pot.size();
		}
		return sum;
	}

	public synchronized void act(Subscriber subscriber, Action action, double amount) {
		int index = players.indexOf(subscriber.getName());
		if (index == -1) {
			return;
		}
		final Player player = players.get(index);
		if (!player.equals(players.currentActor())) {
			return;
		}

		// cancel timer if exists
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		timerCounter = 0;

		// rounding (to 0.01 + if over all-in, to all-in)
		amount = ((double) Math.round(amount * 100)) / 100;
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
			s.updateTimer(timerCounter);
		}

		// if there is none to act or after fold there is only one person with cards
		if (playersToAct < 1 || (action == Action.FOLD && players.countPlayersWithCards() <= 1)) {
			endRound();
		} else {

			final Player actor = players.nextActor();
			final Player clone = actor.publicClone(false);
			for (Subscriber s : subscribers) {
				s.toAct(clone, bet - clone.getBet(), totalPot());
			}
		}

	}

	public synchronized void callClock() {
		if (running && timer == null) {
			timerCounter = 15;
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					timerCounter--;
					if (timerCounter <= 0) {
						players.currentActor().setAway(true);
						act(sittingSubs[players.indexOf(players.currentActor())], Action.FOLD, 0);
						timer.cancel();
						timer.purge();
						timer = null;
					}
					for (Subscriber s : subscribers) {
						s.updateTimer(timerCounter);
					}
				}

			}, 0, 1000);

		}
	}

	public synchronized boolean join(Subscriber subscriber) {
		int index = players.indexOf(subscriber.getName());

		if (index != -1) {

			// handle case where player is already created for this subscriber
			if (sittingSubs[index] != null) {
				subscribers.remove(sittingSubs[index]);
			}
			sittingSubs[index] = subscriber;
			subscriber.acceptSeat(index);
		} else {

			// handle case where subscriber is not really in game, but has buyin
			subscriber.updateBankroll(db.buyout(subscriber.getName()));

		}
		subscribers.add(subscriber);
		fullRefresh(subscriber);
		return true;
	}

	public synchronized boolean sit(Subscriber subscriber, int seat, double buyin) {
		int index = players.indexOf(subscriber.getName());
		if (index != -1) {
			return false;
		}
		if (players.get(seat) != null) {
			return false;
		}
		if (buyin <= blind) {
			return false;
		}

		final Player p = new Player(subscriber.getName(), buyin);
		subscriber.updateBankroll(db.buyin(subscriber.getName(), buyin));
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

	public synchronized void stand(Subscriber subscriber) {
		int index = players.indexOf(subscriber.getName());
		if (index == -1) {
			return;
		}

		Player player = players.get(index);
		if (player.hasCards()) {
			return;
		}

		sittingSubs[index] = null;
		subscriber.updateBankroll(db.buyout(player.getName()));
		final Player clone = player.publicClone(false);
		for (Subscriber s : subscribers) {
			s.removePlayer(clone);
		}
		players.remove(player);

	}

	public synchronized boolean setAway(Subscriber subscriber, boolean away) {
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
