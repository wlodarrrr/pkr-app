package chat;

import java.util.Set;

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

	default void register() {
		ChatServer.register(this);
	}

	default void send(String message) {
		ChatServer.broadcast(getName(), message);
	}

	String getName();

}