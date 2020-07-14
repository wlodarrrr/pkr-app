package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Database {
	private static final String PASS = "d394d09d5161ad351b9a63e148af0f8d69ead6e6e6a11c0fbfded36d1529153c";
	private static final String LOGIN = "ducagnqdispwsr";
	private static final String URL = "jdbc:postgresql://ec2-54-234-28-165.compute-1.amazonaws.com:5432/dcni3mskoi8cln";
	static final Set<DbClient> clients = new HashSet<>();

	static void deregister(DbClient bc) {
		if (Database.clients.contains(bc)) {
			Database.clients.remove(bc);
		}
	}

	private static DbClient getClientOf(String name) {
		if (name == null) {
			return null;
		}
		for (DbClient bc : clients) {
			if (name.contentEquals(bc.getName())) {
				return bc;
			}
		}
		return null;
	}

	static void register(DbClient bc) {
		clients.add(bc);
		double amount = 0;
		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
			Statement s = conn.createStatement();
			String sql = "select cash from bankroll where name = '" + bc.getName() + "';";
			ResultSet executeQuery = s.executeQuery(sql);
			if (executeQuery.next()) {
				amount = executeQuery.getDouble(1);
			} else {
				conn.close();
				throw new IllegalStateException("User not created!!");
			}

			bc.bankrollChanged(amount);
			conn.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void updateBuyin(String name, double amount) {
		try {
			// connect database
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
			Statement s = conn.createStatement();

			// update player's cash on table
			String sql = "update bankroll set buyin = " + amount + " where name = '" + name + "';";
			s.executeUpdate(sql);

			// close database
			conn.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void massUpdateBuyin(Map<String, Double> buyins) {
		try {
			// connect database
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
			Statement s = conn.createStatement();

			// update players cash on table
			for (String str : buyins.keySet()) {
				String sql = "update bankroll set buyin = " + buyins.get(str) + " where name = '" + str + "';";
				s.executeUpdate(sql);
			}

			// close database
			conn.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void buyin(String name, double amount) {

		try {
			// connect database
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
			Statement s = conn.createStatement();

			// buy in player
			String sql = "update bankroll set cash = cash - " + amount + ",buyin = buyin + " + amount + "where name = '"
					+ name + "';";
			s.executeUpdate(sql);

			// inform client about change of his bank roll
			String result = "select cash from bankroll where name = '" + name + "';";
			ResultSet rs = s.executeQuery(result);
			final DbClient c = Database.getClientOf(name);
			if (c != null) {
				rs.next();
				c.bankrollChanged(rs.getDouble(1));
			}

			// close database
			conn.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void buyout(String name) {

		try {
			// connect database
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
			Statement s = conn.createStatement();

			// buy out player
			String sql = "update bankroll set cash = cash + buyin ,buyin = 0 where name = '" + name + "';";
			s.executeUpdate(sql);

			// inform client about change of his bank roll
			String result = "select cash from bankroll where name = '" + name + "';";
			ResultSet rs = s.executeQuery(result);
			final DbClient c = Database.getClientOf(name);
			if (c != null) {
				rs.next();
				c.bankrollChanged(rs.getDouble(1));
			}

			// close database
			conn.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static boolean auth(String name, String pass) {
		boolean success = false;
		// connect database
		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
			Statement s = conn.createStatement();

			String sql = "select pass from bankroll where name = '" + name + "';";
			ResultSet rs = s.executeQuery(sql);
			if (!rs.next()) {
				success = false;
			} else {
				String dbPass = rs.getString(1);
				if (dbPass == null) {
					String sql2 = "update bankroll set pass ='" + pass + "' where name = '" + name + "'";
					s.executeUpdate(sql2);
					success = true;
				} else {
					success = dbPass.contentEquals(pass);
				}
			}
			conn.close();
			return success;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	static boolean createUser(String name, String pass) {
		// TODO
		return false;
	}
}
