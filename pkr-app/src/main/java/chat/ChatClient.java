package chat;

import java.util.Set;

import db.Database;

public interface ChatClient {

	default void deregister() {
		ChatServer.deregister(this);
	}

	default Set<String> getNames() {
		return ChatServer.getNames();
	}

	void joined(String name);

	void left(String name);

	void receive(String sender, String message);

	default boolean register(String name, String pass) {
		boolean auth = Database.auth(name, pass);
		if (auth) {
			ChatServer.register(this);
		}
		return auth;
	}

	default void send(String message) {
		ChatServer.broadcast(getName(), message);
	}

	String getName();

}