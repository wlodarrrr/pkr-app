package com.pkr;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import bank.BankClient;

public class LoginView extends VerticalLayout implements BankClient {

	private final Button bLogin;
	private final MainView mv;
	private final TextField tfBankroll;
	private final TextField tfLogin;
	private PasswordField tfPass;
	private boolean logged;

	public LoginView(MainView mv) {
		this.mv = mv;
		setWidth("200px");
		setHeight("250px");
		addClassNames("box");

		tfLogin = new TextField("Login");
		tfPass = new PasswordField("Password");

		tfBankroll = new TextField("Bankroll");
		tfBankroll.setVisible(false);

		bLogin = new Button("Login", e -> log());
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
			mv.login(tfLogin.getValue(), tfPass.getValue());
		}
	}

	public void login(boolean login) {
		register();
		tfLogin.setEnabled(!login);
		tfPass.setVisible(!login);
		tfBankroll.setVisible(login);
		bLogin.setText(login ? "Logout" : "Login");
		logged = login;
	}
}
