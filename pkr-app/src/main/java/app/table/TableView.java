package app.table;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import app.game.Game;
import app.game.Player;
import app.game.Subscriber;
import app.utils.AbsoluteLayout;
import app.utils.Card;
import app.utils.TextConstants;

public class TableView extends AbsoluteLayout implements Subscriber {

	private static final Card[] facedownCards = new Card[] { new Card(), new Card() };
	private static final int size = 10;
	private final TableBet[] bets = new TableBet[TableView.size];
	private TableBoard board;
	private Button bStandUp;
	private TableBet pot;
	private int seat;
	private final TableSeat[] seats = new TableSeat[TableView.size];
	private TextField tfBuyIn;
	private TableActions actions;
	private Checkbox cbAway;
	private Game game;
	private UI ui;
	private String name;

	public TableView(@Autowired Game game, String name) {
		this.game = game;
		this.name = name;
		createGUI();

		cbAway.addValueChangeListener(e -> game.setAway(this, e.getValue()));
		bStandUp.addClickListener(e -> game.stand(this));

		for (int i = 0; i < TableView.size; i++) {
			final int index = i;
			seats[index].setClickable(true);
			seats[index].addClickListener(e -> tryToSit(index));
		}

	}

	private void tryToSit(int index) {
		double buyin = 0;
		try {
			buyin = Double.parseDouble(tfBuyIn.getValue());
		} catch (final Exception ex) {
		}
		boolean sitting = game.sit(this, index, buyin);
		if (sitting) {
			setSitting(sitting, index);
		}
	}

	private void setSitting(boolean sitting, int seat) {
		tfBuyIn.setVisible(!sitting);
		tfBuyIn.setValue("");
		cbAway.setVisible(sitting);
		bStandUp.setVisible(sitting);
		this.seat = seat;
	}

	private void createGUI() {
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

		pot = new TableBet();
		add(pot, 400, 535);

		board = new TableBoard();
		add(board, 280, 430);

		actions = new TableActions(game, this);
		add(actions, 750, 440);

		tfBuyIn = new TextField(TextConstants.BUY_IN);
		cbAway = new Checkbox(TextConstants.AWAY, false);
		cbAway.setVisible(false);

		bStandUp = new Button(TextConstants.STAND_UP);
		bStandUp.setVisible(false);

		final VerticalLayout vl = new VerticalLayout(tfBuyIn, cbAway, bStandUp);
		vl.setWidth("200px");
		vl.setHeight("100px");
		vl.addClassNames("box");
		add(vl, 0, 0);
	}

	@Override
	public void toAct(Player player, double toCall, double pot) {
		ui.access(() -> {
			int index = player.getSeat();
			for (int i = 0; i < size; i++) {
				seats[i].setActive(i == index);
			}
			updatePot(pot);
			if (index == seat) {
				actions.setVisible(true);
				actions.update(toCall, pot, player.getCash());
			} else {
				actions.setVisible(false);
			}

		});

	}

	@Override
	public void updateBoard(Card[] cards) {
		ui.access(() -> {
			board.update(cards);
		});

	}

	@Override
	public void updateDealer(int dealerPosition) {
		ui.access(() -> {
			for (int i = 0; i < TableView.size; i++) {
				seats[i].setDealer(i == dealerPosition);
			}
		});
	}

	@Override
	public void updatePot(double totalPot) {
		ui.access(() -> {
			pot.update(totalPot);
		});
	}

	@Override
	public void updateHoleCards(Card[] cards) {
		if (seat != -1) {
			ui.access(() -> {
				seats[seat].setCards(cards);
			});
		}

	}

	@Override
	public void updatePlayer(Player player) {
		ui.access(() -> {
			final int index = player.getSeat();
			seats[index].setName(player.getName());
			seats[index].setChips(player.getCash());
			seats[index].setAway(player.isAway());
			seats[index].setClickable(false);
			bets[index].update(player.getBet());

			if (player.hasCards()) {
				if (this.seat != index) {
					seats[index].setCards(facedownCards);
				}
			} else {
				seats[index].setCards(null);
			}
		});
	}

	@Override
	public void removePlayer(Player player) {
		ui.access(() -> {
			final int index = player.getSeat();
			seats[index].setName("");
			seats[index].setChips(0);
			seats[index].setAway(false);
			bets[index].update(0);
			seats[index].setCards(null);
			seats[index].setClickable(true);

			if (seat == index) {
				setSitting(false, -1);
			}
		});
	}

	@Override
	public void doShowdown(Set<Player> playersToShow, Card[] board) {
		ui.access(() -> {
			actions.setVisible(false);
			updatePot(0);
			updateBoard(board);
			for (Player player : playersToShow) {
				final int seat = player.getSeat();
				seats[seat].setName(player.getName());
				seats[seat].setChips(player.getCash());
				seats[seat].setAway(player.isAway());
				seats[seat].setClickable(false);
				seats[seat].setActive(false);
				bets[seat].update(player.getBet());

				Card[] cards = player.getCards();
				if (cards != null) {
					seats[seat].setCards(cards);
				} else if (player.hasCards()) {
					seats[seat].setCards(facedownCards);
				} else {
					seats[seat].setCards(null);
				}
			}
			for (int i = 0; i < size; i++) {
				seats[i].setActive(false);
			}
		});
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		this.ui = attachEvent.getUI();
		game.join(this);
	}

	@Override
	public String getName() {
		return name;
	}
}
