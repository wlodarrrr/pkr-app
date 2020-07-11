package com.pkr;

import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import cards.Card;
import game.Player;
import game.Subscriber;

public class TableView extends AbsoluteLayout implements Subscriber {

	private static final Card[] facedownCards = new Card[] { new Card(), new Card() };
	private static final int size = 10;
	private final TableBet[] bets;
	private final TableBoard board;
	private final Button bStandUp;
	private final MainView mv;
	private final TableBet pot;
	private int seat;
	private final TableSeat[] seats;
	private final TextField tfBuyIn;
	private TableActions actions;
	private Checkbox cbAway;

	public TableView(MainView mv) {
		this.mv = mv;

		seats = new TableSeat[TableView.size];
		bets = new TableBet[TableView.size];
		setSeatsAndBets();

		pot = new TableBet();
		add(pot, 390, 535);

		board = new TableBoard();
		add(board, 280, 430);

		actions = new TableActions(this);
		add(actions, 750, 440);

		tfBuyIn = new TextField("BuyIn");
		cbAway = new Checkbox("Away", false);
		cbAway.addValueChangeListener(e -> {
			setAway(e.getValue());
		});
		bStandUp = new Button("Stand up");
		bStandUp.setEnabled(false);
		bStandUp.addClickListener(e -> {
			stand();
		});
		final VerticalLayout vl = new VerticalLayout(tfBuyIn, cbAway, bStandUp);
		vl.setWidth("200px");
		vl.setHeight("225px");
		vl.addClassNames("box");
		add(vl, 0, 0);

		for (int i = 0; i < TableView.size; i++) {
			final int index = i;
			seats[index].setClickable(true);
			seats[index].addClickListener(e -> sit(index, getBuyIn()));
		}
	}

	private double getBuyIn() {
		try {
			final double d = Double.parseDouble(tfBuyIn.getValue());
			return Math.round(d * 100) / 100;
		} catch (final Exception ex) {
			return 0;
		}
	}

	private void setSeatsAndBets() {
		for (int i = 0; i < TableView.size; i++) {
			seats[i] = new TableSeat(this);
			bets[i] = new TableBet();

		}
		add(seats[0], 0, 440);
		add(bets[0], 160, 440);
		add(seats[1], 0, 630);
		add(bets[1], 160, 630);
		add(seats[2], 100, 800);
		add(bets[2], 260, 800);
		add(seats[3], 275, 970);
		add(bets[3], 338, 830);

		add(seats[4], 450, 800);
		add(bets[4], 420, 800);
		add(seats[5], 550, 630);
		add(bets[5], 520, 630);

		add(seats[6], 550, 440);
		add(bets[6], 520, 440);
		add(seats[7], 450, 270);
		add(bets[7], 420, 270);
		add(seats[8], 275, 100);
		add(bets[8], 333, 240);
		add(seats[9], 100, 270);
		add(bets[9], 260, 270);
	}

	@Override
	public void toAct(Player player, double toCall, double pot) {
		mv.getUI().get().access(() -> {
			int index = player.getSeat();
			for (int i = 0; i < size; i++) {
				seats[i].setActive(i == index);
			}
			updatePot(pot);
			if (index == seat) {
				actions.setVisible(true);
				System.out.println(toCall + " " + pot);
				actions.update(toCall, pot);
			} else {
				actions.setVisible(false);
			}

		});

	}

	@Override
	public void updateBoard(Card[] cards) {
		mv.getUI().get().access(() -> {
			board.update(cards);
		});

	}

	@Override
	public void updateDealer(int dealerPosition) {
		mv.getUI().get().access(() -> {
			for (int i = 0; i < TableView.size; i++) {
				seats[i].setDealer(i == dealerPosition);
			}
		});
	}

	@Override
	public void updateHoleCards(Card[] cards) {
		if (seat != -1) {
			mv.getUI().get().access(() -> {
				seats[seat].setCards(cards);
			});
		}

	}

	@Override
	public void updatePlayer(Player player) {
		mv.getUI().get().access(() -> {
			final int seat = player.getSeat();
			seats[seat].setName(player.getName());
			seats[seat].setChips(player.getCash());
			seats[seat].setAway(player.isAway());
			seats[seat].setClickable(false);
			bets[seat].update(player.getBet());

			if (player.hasCards()) {
				if (this.seat != seat) {
					seats[seat].setCards(facedownCards);
				}
			} else {
				seats[seat].setCards(null);
			}

			if (player.getName().contentEquals(mv.getName())) {
				this.seat = seat;
				tfBuyIn.setReadOnly(true);
				tfBuyIn.setValue("");
				bStandUp.setEnabled(true);
			}

		});
	}

	@Override
	public void updatePot(double totalPot) {
		mv.getUI().get().access(() -> {
			pot.update(totalPot);
		});
	}

	@Override
	public String getName() {
		return mv.getName();
	}

	@Override
	public void logout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePlayer(game.Player player) {
		mv.getUI().get().access(() -> {
			final int seat = player.getSeat();
			seats[seat].setName("");
			seats[seat].setChips(0);
			seats[seat].setAway(false);
			bets[seat].update(0);
			seats[seat].setCards(null);
			seats[seat].setClickable(true);

			if (player.getName().contentEquals(mv.getName())) {
				tfBuyIn.setReadOnly(false);
				bStandUp.setEnabled(false);
			}
		});
	}

	@Override
	public void doShowdown(Set<Player> playersToShow, Card[] board) {
		mv.getUI().get().access(() -> {
			actions.setVisible(false);
			updatePot(0);
			updateBoard(board);
			for (Player player : playersToShow) {
				final int seat = player.getSeat();
				seats[seat].setName(player.getName());
				seats[seat].setChips(player.getCash());
				seats[seat].setAway(player.isAway());
				seats[seat].setClickable(false);
				bets[seat].update(player.getWin());

				Card[] cards = player.getCards();
				if (cards != null) {
					seats[seat].setCards(cards);
				} else if (player.hasCards()) {
					seats[seat].setCards(facedownCards);
				} else {
					seats[seat].setCards(null);
				}
			}
		});
	}
}
