package com.pkr;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.textfield.TextField;

import game.Action;

public class TableActions extends AbsoluteLayout {
	private final Button call;
	private final Button fold;
	private double pot;
	private final Button raise;
	private final TextField raiseSize;
	private double toCall;

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
				tv.doAction(Action.RAISE, Double.parseDouble(raiseSize.getValue()));
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

		// make it beautiful
		final int width = 530 / 4;
		fold.setWidth(width + "px");
		call.setWidth(width + "px");
		raise.setWidth(width + "px");
		raiseSize.setWidth(width + "px");
		x12.setWidth(width + "px");
		x23.setWidth(width + "px");
		x34.setWidth(width + "px");
		xpot.setWidth(width + "px");

		x12.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		x23.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		x34.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		xpot.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		fold.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		call.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		raise.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

		// add components to panel
		add(x12, 0, 0);
		add(x23, 0, width);
		add(x34, 0, width * 2);
		add(xpot, 0, width * 3);
		add(fold, 40, 0);
		add(call, 40, width);
		add(raise, 40, width * 2);
		add(raiseSize, 40, width * 3);

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

	public void update(double toCall, double pot) {
		this.pot = (double) Math.round(pot * 100) / 100;
		this.toCall = (double) Math.round(toCall * 100) / 100;

		if (toCall == 0) {
			call.setText("Check");
		} else {
			call.setText("Call (" + toCall + ")");
		}
	}
}
