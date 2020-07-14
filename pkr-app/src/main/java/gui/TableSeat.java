package gui;

import com.vaadin.flow.component.html.Span;

import cards.Card;

public class TableSeat extends AbsoluteLayout {

	private final Span lChips;
	private final Span lName;

	public TableSeat(TableView tv) {

		addClassNames("box");
		setWidth("130px");
		setHeight("140px");

		lName = new Span("");
		lChips = new Span("");

		lName.addClassName("centered");
		lName.setWidth("140px");
		lName.setHeight("20px");
		lChips.addClassName("centered");
		lChips.setWidth("140px");
		lChips.setHeight("20px");

		add(lName, 110, 0);
		add(lChips, 130, 0);

		addClassName("link");

	}

	public boolean isEmpty() {
		return "".contentEquals(lName.getText());
	}

	public void setActive(boolean isActive) {
		if (isActive) {
			addClassName("active");
		} else {
			removeClassName("active");
		}

	}

	public void setAllIn() {
		lChips.setText("All-In");
	}

	public void setAway(boolean away) {
		if (away) {
			addClassName("away");
		} else {
			removeClassName("away");
		}

	}

	public void setCards(Card[] cards) {
		if (cards == null) {
			removeAll();
			add(lName, 101, 0);
			add(lChips, 126, 0);
		} else {
			removeAll();
			add(lName, 101, 0);
			add(lChips, 126, 0);
			if (cards[0] != null) {
				add(cards[0].getImage(), 0, 0);
				add(cards[1].getImage(), 0, 70);
			}

		}
	}

	public void setChips(double chips) {
		final double rounded = (double) Math.round(chips * 100) / 100;
		lChips.setText(rounded == 0 ? "" : Double.toString(rounded));
	}

	public void setClickable(boolean empty) {
		if (empty) {
			addClassName("link");
		} else {
			removeClassName("link");
		}

	}

	public void setDealer(boolean isDealer) {
		if (isDealer) {
			if (lName.getText().contains("(D)")) {
				return;
			} else {
				lName.setText("(D) " + lName.getText());
			}
		} else {
			if (lName.getText().contains("(D)")) {
				lName.setText(lName.getText().substring(4));
			} else {
				return;
			}
		}

	}

	public void setName(String name) {
		lName.setText(name);
	}
}