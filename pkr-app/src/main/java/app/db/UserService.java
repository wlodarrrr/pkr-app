package app.db;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

	private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

	private UserRepository userRepo;

	public UserService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}

	public List<User> findAll() {
		return userRepo.findAll();
	}

	public void delete(User user) {
		userRepo.delete(user);
	}

	public void save(User user) {
		if (user == null) {
			LOGGER.log(Level.SEVERE, "No data for user.");
			return;
		}
		userRepo.save(user);
	}

	@PostConstruct
	public void mockTestData() {
		if (findAll().size() == 0) {
			String[] logins = new String[] { "John", "Travolta", "Andrzej" };
			for (String s : logins) {
				User u = new User();
				u.setName(s);
				u.setPass(getPasswordEncoder().encode(s));
				save(u);
			}
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userRepo.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}

		return new org.springframework.security.core.userdetails.User(user.getName(), user.getPass(),
				List.of(new SimpleGrantedAuthority("user")));
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public boolean register(String username, String password) {
		if (username.length() < 3 || username.length() > 25) {
			return false;
		}
		if (password.length() < 3 || password.length() > 25) {
			return false;
		}
		if (userRepo.findByUsername(username) != null) {
			return false;
		}
		User user = new User();
		user.setName(username);
		user.setPass(getPasswordEncoder().encode(password));
		userRepo.save(user);

		return true;
	}

	public List<Debt> organize(Set<User> selectedItems) {
		List<User> sel = new ArrayList<User>();
		List<Debt> debts = new ArrayList<Debt>();
		sel.addAll(selectedItems);
		while (sel.size() > 1) {
			User minUser = null;
			User maxUser = null;
			double min = 0;
			double max = 0;
			for (User u : sel) {
				min = Math.min(min, u.getCash());
				if (min == u.getCash()) {
					minUser = u;
				}
				max = Math.max(max, u.getCash());
				if (max == u.getCash()) {
					maxUser = u;
				}
			}
			double debt = Math.min(max, -min);
			Debt d = new Debt(maxUser.getName(), minUser.getName(), debt, LocalDate.now());
			minUser.setCash(minUser.getCash() + debt);
			maxUser.setCash(maxUser.getCash() - debt);
			if (minUser.getCash() == 0) {
				sel.remove(minUser);
			}
			if (maxUser.getCash() == 0) {
				sel.remove(maxUser);
			}
			debts.add(d);
			userRepo.save(minUser);
			userRepo.save(maxUser);
		}

		return debts;
	}
}
