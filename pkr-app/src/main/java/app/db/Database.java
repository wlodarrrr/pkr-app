package app.db;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;

@Service
public class Database {

	public boolean authenticate(LoginEvent e) {
		return e.getPassword().length() > 2;

	}

	public void updateBuyin(String name, double cash) {
		// TODO Auto-generated method stub

	}

	public void buyout(String name) {
		// TODO Auto-generated method stub

	}

	public void buyin(String name, double buyin) {
		// TODO Auto-generated method stub

	}

	public void massUpdateBuyin(Map<String, Double> buyins) {
		// TODO Auto-generated method stub

	}
}
