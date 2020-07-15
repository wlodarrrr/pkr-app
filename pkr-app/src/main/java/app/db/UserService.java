package app.db;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	List<User> findAll() {
		return jdbcTemplate.query("select name,pass,cash,buyin from bankroll;", (rs, rowNum) -> {
			return new User(rs.getString("name"), rs.getString("pass"), rs.getDouble("cash"), rs.getDouble("buyin"));
		});
	}

	User find(String name) {
		List<User> result = jdbcTemplate.query("select name,pass,cash,buyin from bankroll where name = ?;",
				new Object[] { name }, (rs, rowNum) -> {
					return new User(rs.getString("name"), rs.getString("pass"), rs.getDouble("cash"),
							rs.getDouble("buyin"));
				});
		if (result.size() > 0) {
			return result.get(0);
		} else {
			return null;
		}
	}
}
