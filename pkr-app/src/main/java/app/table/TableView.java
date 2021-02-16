package app.table;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import app.chat.ChatView;
import app.game.Game;
import app.game.Player;
import app.game.Subscriber;
import app.utils.AbsoluteLayout;
import app.utils.AudioPlayer;
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
	private TextField tfBankroll;
	private Span timer;
	private AudioPlayer chipsSound;
	private AudioPlayer turnSound;
	private AudioPlayer timerSound;

	public TableView(@Autowired Game game) {
		this.game = game;
		addClassNames("bg");
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
		setWidth("1280px");
		setHeight("720px");
		addClassNames("table");
		for (int i = 0; i < TableView.size; i++) {
			seats[i] = new TableSeat(this);
			bets[i] = new TableBet();

		}
		add(seats[0], -20, 270);
		add(bets[0], 140, 270);
		bets[0].setJustifyContentMode(JustifyContentMode.CENTER);
		add(seats[1], -20, 430);
		add(bets[1], 140, 430);
		bets[1].setJustifyContentMode(JustifyContentMode.CENTER);

		add(seats[2], 30, 590);
		add(bets[2], 190, 590);
		bets[2].setJustifyContentMode(JustifyContentMode.START);

		add(seats[3], 200, 700);
		add(bets[3], 260, 550);
		bets[3].setJustifyContentMode(JustifyContentMode.END);

		add(seats[4], 370, 590);
		add(bets[4], 340, 590);
		bets[4].setJustifyContentMode(JustifyContentMode.START);

		add(seats[5], 420, 430);
		add(bets[5], 390, 430);
		bets[5].setJustifyContentMode(JustifyContentMode.CENTER);
		add(seats[6], 420, 270);
		add(bets[6], 390, 270);
		bets[6].setJustifyContentMode(JustifyContentMode.CENTER);

		add(seats[7], 370, 110);
		add(bets[7], 340, 110);
		bets[7].setJustifyContentMode(JustifyContentMode.END);

		add(seats[8], 200, 0);
		add(bets[8], 260, 160);
		bets[8].setJustifyContentMode(JustifyContentMode.START);

		add(seats[9], 30, 110);
		add(bets[9], 190, 110);
		bets[9].setJustifyContentMode(JustifyContentMode.END);

		pot = new TableBet();
		add(pot, 190, 350);

		board = new TableBoard();
		add(board, 220, 245);

		actions = new TableActions(game, this);
		add(actions, 620, 150);

		tfBankroll = new TextField(TextConstants.BANKROLL);
		tfBankroll.setEnabled(false);
		tfBankroll.setSizeFull();
		tfBuyIn = new TextField(TextConstants.BUY_IN);
		tfBuyIn.setSizeFull();
		cbAway = new Checkbox(TextConstants.AWAY, false);
		cbAway.setVisible(false);

		bStandUp = new Button(TextConstants.STAND_UP);
		bStandUp.setVisible(false);

		final VerticalLayout vl = new VerticalLayout(tfBankroll, tfBuyIn, cbAway, bStandUp);
		vl.setWidth("200px");
		vl.setHeight("200px");
		vl.addClassNames("box");
		add(vl, -20, 1000);

		timer = new Span(" ");
		timer.addClassName("bigfont");
		Button bTimer = new Button("Call clock", e -> game.callClock());
		final VerticalLayout vl2 = new VerticalLayout(timer, bTimer);
		vl2.setWidth("140px");
		vl2.setHeight("200px");
		vl2.addClassNames("box");
		add(vl2, -20, 850);

		ChatView cv = new ChatView();
		add(cv, 190, 850);

		Button fill = new Button("Fill", e -> fill());
		add(fill, 0, 1300);

		Button sound = new Button("Fill", e -> chipsSound.play());
		add(sound, 0, 1400);

		chipsSound = new AudioPlayer();
		chipsSound.setSource("chips.mp3");
		add(chipsSound);

		turnSound = new AudioPlayer();
		turnSound.setSource("turn.mp3");
		add(turnSound);

		timerSound = new AudioPlayer();
		timerSound.setSource("timer.mp3");
		add(timerSound);
	}

	private void createGUIbackup() {
		for (int i = 0; i < TableView.size; i++) {
			seats[i] = new TableSeat(this);
			bets[i] = new TableBet();

		}
		add(seats[0], 0, 320);
		add(bets[0], 160, 320);
		bets[0].setJustifyContentMode(JustifyContentMode.CENTER);
		add(seats[1], 0, 480);
		add(bets[1], 160, 480);
		bets[1].setJustifyContentMode(JustifyContentMode.CENTER);
		add(seats[2], 0, 640);
		add(bets[2], 160, 640);
		bets[2].setJustifyContentMode(JustifyContentMode.CENTER);

		add(seats[3], 50, 800);
		add(bets[3], 210, 800);
		bets[3].setJustifyContentMode(JustifyContentMode.START);

		add(seats[4], 220, 910);
		add(bets[4], 290, 760);
		bets[4].setJustifyContentMode(JustifyContentMode.END);

		add(seats[5], 390, 800);
		add(bets[5], 360, 800);
		bets[5].setJustifyContentMode(JustifyContentMode.START);

		add(seats[6], 440, 640);
		add(bets[6], 415, 640);
		bets[6].setJustifyContentMode(JustifyContentMode.CENTER);
		add(seats[7], 440, 480);
		add(bets[7], 415, 480);
		bets[7].setJustifyContentMode(JustifyContentMode.CENTER);

		add(seats[8], 220, 50);
		add(bets[8], 290, 210);
		bets[8].setJustifyContentMode(JustifyContentMode.START);

		add(seats[9], 50, 160);
		add(bets[9], 210, 160);
		bets[9].setJustifyContentMode(JustifyContentMode.END);

		pot = new TableBet();
		add(pot, 250, 470);

		board = new TableBoard();
		add(board, 280, 365);

		actions = new TableActions(game, this);
		add(actions, 630, 500);

		tfBankroll = new TextField(TextConstants.BANKROLL);
		tfBankroll.setEnabled(false);
		tfBankroll.setSizeFull();
		tfBuyIn = new TextField(TextConstants.BUY_IN);
		tfBuyIn.setSizeFull();
		cbAway = new Checkbox(TextConstants.AWAY, false);
		cbAway.setVisible(false);

		bStandUp = new Button(TextConstants.STAND_UP);
		bStandUp.setVisible(false);

		final VerticalLayout vl = new VerticalLayout(tfBankroll, tfBuyIn, cbAway, bStandUp);
		vl.setWidth("140px");
		vl.setHeight("200px");
		vl.addClassNames("box");
		add(vl, 0, 0);

		timer = new Span(" ");
		timer.addClassName("bigfont");
		Button bTimer = new Button("Call clock", e -> game.callClock());
		final VerticalLayout vl2 = new VerticalLayout(timer, bTimer);
		vl2.setWidth("140px");
		vl2.setHeight("200px");
		vl2.addClassNames("box");
		add(vl2, 0, 960);

		ChatView cv = new ChatView();
		add(cv, 440, 0);

		// Button fill = new Button("Fill", e -> fill());
		// add(fill, 0, 1200);
	}

	private void fill() {
		for (int i = 0; i < size; i++) {
			seats[i].setCards(facedownCards);
			seats[i].setName("player" + i);
			seats[i].setChips(i);
			bets[i].update(i * 1.23);
		}

		board.update(facedownCards);

		actions.setVisible(true);
	}

	@Override
	public void toAct(Player player, double toCall, double pot) {
		ui.access(() -> {
			chipsSound.play();

			int index = player.getSeat();
			for (int i = 0; i < size; i++) {
				seats[i].setActive(i == index);
			}
			updatePot(pot);
			if (index == seat) {
				turnSound.play();
				actions.setVisible(true);
				actions.update(toCall, pot, player.getCash());
				ui.getPage().setTitle(TextConstants.YOUR_MOVE);
			} else {
				actions.setVisible(false);
				ui.getPage().setTitle("Pkr");
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
	public void updateHoleCards(final Card[] cards) {
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
				if (player.getCash() == 0) {
					seats[index].setAllIn();
				}
				if (this.seat != index) {
					seats[index].setCards(facedownCards);
				}
			} else {
				seats[index].setCards(null);
			}
		});
	}

	@Override
	public void updateBankroll(double amount) {
		ui.access(() -> tfBankroll.setValue(Double.toString(((double) Math.round(amount * 100)) / 100)));
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
			timer.setText("");
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
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	@Override
	public void updateTimer(int timerCounter) {
		ui.access(() -> {
			if (timerCounter <= 0) {
				timer.setText("");
				ui.getPage().setTitle("Pkr");
			} else {
				timer.setText(timerCounter + "");
				ui.getPage().setTitle(TextConstants.TIMER + ": " + timerCounter);
				if (timerCounter == 14) {
					timerSound.play();
				}
			}
		});
	}

	@Override
	public void acceptSeat(int index) {
		seat = index;
	}
}
