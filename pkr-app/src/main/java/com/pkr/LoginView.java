package com.pkr;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import cards.TextConstants;
import db.Database;
import db.DbClient;

public class LoginView extends VerticalLayout implements DbClient {

	private final Button bLogin;
	private final MainView mv;
	private final TextField tfBankroll;
	private final TextField tfLogin;
	private PasswordField tfPass;
	private boolean logged;

	public LoginView(MainView mv) {
		this.mv = mv;
		setWidth("200px");
		setHeight("300px");
		addClassNames("box");

		tfLogin = new TextField(TextConstants.LOGIN);
		tfPass = new PasswordField(TextConstants.PASSWORD);

		tfBankroll = new TextField(TextConstants.BANKROLL);
		tfBankroll.setVisible(false);

		bLogin = new Button(TextConstants.LOG_IN, e -> log());
		logged = false;

		add(tfLogin, tfPass, tfBankroll, bLogin);

	}

	@Override
	public void bankrollChanged(double bankroll) {
		double d = Math.round(bankroll * 100) / 100;
		tfBankroll.setValue(Double.toString(d));

	}

	public String getName() {
		return tfLogin.getValue();
	}

	private void log() {
		if (logged) {
			mv.logout();
		} else {
			boolean auth = Database.auth(tfLogin.getValue(), tfPass.getValue());
			if (auth) {
				mv.login(tfLogin.getValue(), tfPass.getValue());
			} else {
				Notification.show(TextConstants.INCORRECT_LOGIN_PASS_COMBINATION);
			}
		}
	}

	public void login(boolean loggedIn) {
		if (loggedIn) {
			register(tfLogin.getValue(), tfPass.getValue());
		}
		tfLogin.setEnabled(!loggedIn);
		tfPass.setVisible(!loggedIn);
		tfBankroll.setVisible(loggedIn);
		bLogin.setText(loggedIn ? TextConstants.LOG_OUT : TextConstants.LOG_IN);
		logged = loggedIn;
	}
}
