package db;

public interface DbClient {

	void bankrollChanged(double bankroll);

	default void deregister() {
		Database.deregister(this);
	}

	default boolean register(String name, String pass) {
		boolean success = Database.auth(name, pass);
		if (success) {
			Database.register(this);
		}
		return success;
	}

	String getName();

	default boolean createUser(String name, String pass) {
		return Database.createUser(name, pass);
	}
}