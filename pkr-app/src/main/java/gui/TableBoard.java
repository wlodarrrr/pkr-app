package gui;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import cards.Card;

public class TableBoard extends HorizontalLayout {

	public TableBoard() {
		addClassNames("board", "box");
		setWidth("350px");
		setHeight("107px");
		setSpacing(false);
	}

	public void update(Card[] cards) {
		removeAll();
		if (cards != null) {
			for (final Card card : cards) {
				if (card != null) {

					add(card.getImage());
				}
			}
		}
	}
}
