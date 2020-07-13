package com.pkr;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import game.Action;

public class TableActions extends VerticalLayout {
	private final Button call;
	private final Button fold;
	private double pot;
	private final Button raise;
	private final TextField raiseSize;
	private double toCall;
	private double allin;

	public TableActions(TableView tv) {
		// actions
		fold = new Button("Fold", e -> {
			tv.doAction(Action.FOLD, 0);
		});
		call = new Button("Call", e -> {
			tv.doAction(Action.CALL, 0);
		});
		raiseSize = new TextField();
		raise = new Button("Raise", e -> {
			try {
				double d = Double.parseDouble(raiseSize.getValue());
				tv.doAction(Action.RAISE, d);
			} catch (final Exception ex) {
			} finally {
				raiseSize.setValue("");
			}
		});

		// bet sizing
		final Button x12 = new Button("1/2", e -> {
			final double d = betSizing((double) 1 / 2);
			raiseSize.setValue(Double.toString(d));
		});
		final Button x23 = new Button("2/3", e -> {
			final double d = betSizing((double) 2 / 3);
			raiseSize.setValue(Double.toString(d));
		});
		final Button x34 = new Button("3/4", e -> {
			final double d = betSizing((double) 3 / 4);
			raiseSize.setValue(Double.toString(d));
		});
		final Button xpot = new Button("pot", e -> {
			final double d = betSizing(1);
			raiseSize.setValue(Double.toString(d));
		});
		final Button xallin = new Button("all-in", e -> {
			raiseSize.setValue(Double.toString((double) Math.round(allin * 100) / 100));
		});

		// make it beautiful
		final int width = 530;
		fold.setWidth(width / 4 + "px");
		call.setWidth(width / 4 + "px");
		raise.setWidth(width / 4 + "px");
		raiseSize.setWidth(width / 4 + "px");
		x12.setWidth(width / 5 + "px");
		x23.setWidth(width / 5 + "px");
		x34.setWidth(width / 5 + "px");
		xpot.setWidth(width / 5 + "px");
		xallin.setWidth(width / 5 + "px");

		x12.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		x23.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		x34.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		xpot.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		fold.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		call.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		raise.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

		// add components to panel
		HorizontalLayout hl = new HorizontalLayout(x12, x23, x34, xpot, xallin);
		hl.setSpacing(false);
		HorizontalLayout hl2 = new HorizontalLayout(fold, call, raise, raiseSize);
		hl2.setSpacing(false);
		add(hl, hl2);

		// turn off at start
		setVisible(false);

	}

	private double betSizing(double quotient) {
		final double potAfterCall = pot + toCall;
		final double betSize = quotient * potAfterCall + toCall;
		final double roundedBetSize = (double) Math.round(betSize * 100) / 100;
		System.out.println(potAfterCall);
		return roundedBetSize;
	}

	public void update(double toCall, double pot, double allin) {
		this.pot = pot;
		this.toCall = toCall;
		this.allin = allin;

		if (toCall == 0) {
			call.setText("Check");
		} else if (toCall >= allin) {
			call.setText("All-in (" + (double) Math.round(allin * 100) / 100 + ")");
		} else {
			call.setText("Call (" + (double) Math.round(toCall * 100) / 100 + ")");
		}
	}
}
