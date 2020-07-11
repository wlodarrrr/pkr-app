package com.pkr;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import bank.BankClient;

public class LoginView extends VerticalLayout implements BankClient {

	private final Button bLogin;
	private final MainView mv;
	private final TextField tfBankroll;
	private final TextField tfLogin;

	public LoginView(MainView mv) {
		this.mv = mv;
		setWidth("200px");
		setHeight("225px");
		addClassNames("box");

		tfLogin = new TextField("Hello...?");

		tfBankroll = new TextField("Bankroll");
		tfBankroll.setReadOnly(true);

		bLogin = new Button("Login", e -> log());

		add(tfLogin, tfBankroll, bLogin);

	}

	@Override
	public void bankrollChanged(double bankroll) {
		bankroll = Math.round(bankroll * 100) / 100;
		tfBankroll.setValue(Double.toString(bankroll));

	}

	public String getName() {
		return tfLogin.getValue();
	}

	private void log() {
		final boolean logged = "Logout".contentEquals(bLogin.getText());
		if (logged) {
			mv.logout();
		} else {
			mv.login(tfLogin.getValue());
		}
	}

	public void login() {
		register();
		tfLogin.setEnabled(false);
		bLogin.setText("Logout");
	}

	public void logout() {
		deregister();
		tfLogin.setEnabled(true);
		bLogin.setText("Login");
		tfBankroll.setValue("");
	}
}
