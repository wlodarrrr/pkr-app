package app.db;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;

@Service
public class Database {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	List<User> findAll() {
		return jdbcTemplate.query("select name,pass,cash,buyin from bankroll;", (rs, rowNum) -> {
			return new User(rs.getString("name"), rs.getString("pass"), rs.getDouble("cash"), rs.getDouble("buyin"));
		});
	}

	public boolean authenticate(LoginEvent e) {
		return e.getPassword().length() > 2;

	}

	public void buyout(String name) {
		jdbcTemplate.update("update bankroll set cash = cash + buyin,buyin=0 where name = ?", name);

	}

	public double buyin(String name, double buyin) {
		jdbcTemplate.update("update bankroll set cash = cash-?,buyin = ?;", new Object[] { buyin, buyin });
		List<Double> cash = jdbcTemplate.query("select cash from bankroll where name = ?;", new Object[] { name },
				(rs, rowNum) -> {
					return rs.getDouble("cash");
				});
		if (cash.size() > 0) {
			return cash.get(0);
		} else {
			return 0;
		}

	}

	public void updateBuyin(String name, double cash) {
		jdbcTemplate.update("update bankroll set buyin = ? where name = ?", new Object[] { cash, name });

	}

	public void massUpdateBuyin(Map<String, Double> buyins) {
		for (String name : buyins.keySet()) {
			jdbcTemplate.update("update bankroll set buyin = ? where name = ?",
					new Object[] { buyins.get(name), name });
		}

	}
}
