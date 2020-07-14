package db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<User>

			findAll() {
		return jdbcTemplate.query("SELECT name, pass, cash, buyin from bankroll;",
				(rs, rowNum) -> new User(rs.getString("name"), rs.getString("pass"), rs.getDouble("cash"),
						rs.getDouble("buyin")));
	}

	public void update(User user) {
		jdbcTemplate.update("UPDATE bankroll SET cash = ?, buyin =? WHERE name = ?", user.getCash(), user.getBuyin(),
				user.getName());
	}

}