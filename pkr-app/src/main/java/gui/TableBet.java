package gui;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class TableBet extends HorizontalLayout {

	private final Image chipsImage = new Image("coins.png", "");
	private final Span lBet;

	public TableBet() {
		addClassName("bets");
		setWidth("140px");
		setHeight("25px");
		setJustifyContentMode(JustifyContentMode.CENTER);

		lBet = new Span("");
	}

	public void update(double amount) {
		removeAll();
		if (amount > 0) {
			add(chipsImage);
			add(lBet);
			double am = amount * 100;
			am = Math.round(am);
			am = am / 100;

			lBet.setText(Double.toString(am));
		}
	}

}
