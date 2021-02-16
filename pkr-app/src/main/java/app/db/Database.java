package app.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;

@Service
public class Database {

	@Autowired
	private UserRepository userRepository;

	public double buyout(String name) {
		try {
			User user = userRepository.findById(name).get();
			user.setCash(user.getCash() + user.getBuyin());
			user.setBuyin(0);
			userRepository.save(user);
			return user.getCash();
		} catch (NoSuchElementException ex) {
			return 0;
		}
	}

	public double buyin(String name, double buyin) {
		try {
			User user = userRepository.findById(name).get();
			user.setCash(user.getCash() - buyin);
			user.setBuyin(buyin);
			userRepository.save(user);
			return user.getCash();
		} catch (NoSuchElementException ex) {
			return 0;
		}
	}

	public void updateBuyin(String name, double buyin) {
		try {
			User user = userRepository.findById(name).get();
			user.setBuyin(buyin);
			userRepository.save(user);
		} catch (NoSuchElementException e) {
		}
	}

	public void massUpdateBuyin(Map<String, Double> buyins) {
		for (String name : buyins.keySet()) {
			updateBuyin(name, buyins.get(name));
		}

	}

	public List<User> findAll() {
		List<User> users = new ArrayList<User>();
		Iterable<User> result = userRepository.findAll();
		result.forEach(users::add);
		return users;
	}

}
