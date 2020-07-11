package game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import bank.BankServer;
import cards.Card;
import cards.Deck;

public class Game {

	private static final int size = 10;
	private static final Game instance = new Game();;
	private final Set<Subscriber> subscribers = new HashSet<Subscriber>();
	private final Subscriber[] sittingSubs = new Subscriber[size];
	private final Players players = new Players(size);
	private boolean running = false;
	private double blind = 1;
	private int dealerPosition = -1;
	private Card[] board = new Card[5];
	private Actors actors;
	private Player actor;
	private Set<Pot> pots = new HashSet<Pot>();
	private double bet = 0;
	private int playersLeft = 0;
	private int round = 0;

	private void tryStart() {
		if (running) {
			return;
		}

		if (players.getReadyPlayersCount() < 2) {
			return;
		}

		running = true;

		Player dealer = players.after(dealerPosition);
		dealerPosition = players.indexOf(dealer);

		List<Player> readies = players.getReadyPlayers(dealer);
		dealCards(readies);
		actors = new Actors(readies);
		actor = actors.reset();

		// blinds
		double sb = actor.pay(blind / 2);
		addToPot(actor, sb);
		if (actor.isAllin()) {
			actors.remove(actor);
		}
		actor = actors.nextActor();

		double bb = actor.pay(blind);
		addToPot(actor, bb);
		actor = actors.nextActor();

		bet = blind;
		playersLeft = actors.size();

		for (Subscriber s : subscribers) {
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

	private int indexOf(Subscriber subscriber) {
		if (subscriber == null) {
			return -1;
		}
		for (int i = 0; i < size; i++) {
			if (subscriber.equals(sittingSubs[i])) {
				return i;
			}
		}
		return -1;
	}

	private int indexOf(String name) {
		for (int i = 0; i < size; i++) {
			if (players.get(i) != null) {
				if (players.get(i).getName().contentEquals(name)) {
					return i;
				}
			}
		}
		return -1;
	}

	private void endRound() {
		round++;

		// check if it was last round
		if (round == 4) {
			endHand();
			return;
		}

		// reset
		actor = actors.reset();
		bet = 0;

		// count players left
		playersLeft = actors.size();
		if (playersLeft < 2) {
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
		players.resetWins();

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
		Card[] boardToShow = round == 0 ? null : Arrays.copyOf(board, Math.min(round + 2, 5));
		for (Subscriber s : subscribers) {
			s.doShowdown(clonesToShow, boardToShow);
		}

		// cleanup
		players.reset();
		for (int i = 0; i < 5; i++) {
			board[i] = null;
		}
		actor = null;
		actors = null;
		pots.clear();
		bet = 0;
		playersLeft = 0;
		round = 0;

		Timer t = new Timer();
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				running = false;
				tryStart();

			}

		}, 4444);

	}

	private void fullRefresh(Subscriber subscriber) {
		Set<Player> clones = players.getClones();
		for (Player p : clones) {
			subscriber.updatePlayer(p);
		}
		if (running) {
			Card[] boardToShow = round == 0 ? null : Arrays.copyOf(board, Math.min(round + 2, 5));
			subscriber.updateBoard(boardToShow);
			subscriber.updatePot(totalPot());
			subscriber.updateDealer(dealerPosition);

			int i = indexOf(subscriber);
			if (i != -1) {
				subscriber.updateHoleCards(players.get(i).getCards());
			}

			subscriber.toAct(actor.publicClone(false), bet - actor.getBet(), totalPot());
		}
	}

	private double totalPot() {
		double sum = 0;
		for (Pot pot : pots) {
			sum += pot.size();
		}
		return sum;
	}

	void act(Subscriber subscriber, Action action, double amount) {
		int index = indexOf(subscriber);
		if (index == -1) {
			System.out.println("Subscriber is not sitting.");
			return;
		}
		Player player = players.get(index);
		if (actor != player) {
			return;
		}

		double payment = bet - player.getBet();
		switch (action) {
		case FOLD:
			player.resetBet();
			player.setCards(null);

			actors.remove(player);
			playersLeft--;
			break;
		case CALL:

			payment = player.pay(payment);
			addToPot(player, payment);

			if (player.isAllin()) {
				actors.remove(player);
			}
			playersLeft--;
			break;
		case RAISE:

			if (player.getCash() < amount || payment >= amount) {
				System.out.println("Player can't play for that amount");
				return;
			}

			payment = player.pay(amount);
			addToPot(player, payment);
			bet = player.getBet();

			if (player.isAllin()) {
				actors.remove(player);
				playersLeft = actors.size();
			} else {
				playersLeft = actors.size() - 1;
			}
			break;
		}
		Player pClone = player.publicClone(false);
		for (Subscriber s : subscribers) {
			s.updatePlayer(pClone);
		}

		if (playersLeft < 1) {
			endRound();
		} else if (round == 0 && bet == blind && players.countPlayersWithCards() == 1) {
			endRound();
		} else {

			actor = actors.nextActor();
			Player aClone = actor.publicClone(false);
			for (Subscriber s : subscribers) {
				s.toAct(aClone, bet - aClone.getBet(), totalPot());
			}
		}

	}

	void join(Subscriber subscriber) {
		int index = indexOf(subscriber.getName());
		if (index != -1) {
			if (sittingSubs[index] != null) {
				sittingSubs[index].logout();
			}
			sittingSubs[index] = subscriber;
		}
		subscribers.add(subscriber);
		fullRefresh(subscriber);
	}

	void sit(Subscriber subscriber, int seat, double buyin) {
		int index = indexOf(subscriber);
		if (index != -1) {
			System.out.println("Player is already sitting.");
			return;
		}
		if (players.get(seat) != null) {
			System.out.println("Seat is taken.");
			return;
		}

		Player p = new Player(subscriber.getName(), buyin, seat);
		BankServer.increase(subscriber.getName(), -buyin);
		Player clone = p.publicClone(false);
		players.add(p, seat);
		sittingSubs[seat] = subscriber;
		for (Subscriber s : subscribers) {
			s.updatePlayer(clone);
		}
		tryStart();

	}

	void stand(Subscriber subscriber) {
		int index = indexOf(subscriber);
		if (index == -1) {
			System.out.println("Subscriber is not sitting.");
			return;
		}

		Player player = players.get(index);
		if (player.hasCards()) {
			System.out.println("Subscriber is in game.");
			return;
		}

		players.remove(player);
		sittingSubs[index] = null;
		BankServer.increase(player.getName(), player.getCash());
		for (Subscriber s : subscribers) {
			s.removePlayer(player);
		}

	}

	void setAway(Subscriber subscriber, boolean away) {
		int index = indexOf(subscriber);
		if (index != -1) {
			Player player = players.get(index);
			player.setAway(away);
			if (!away) {
				tryStart();
			}
		}
	}

	static Game getInstance() {
		return instance;

	}
}
