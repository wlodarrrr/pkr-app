package chat;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {

	private static final Set<ChatClient> clients = new HashSet<>();

	private static final ExecutorService executor = Executors.newSingleThreadExecutor();

	static void broadcast(String name, String message) {
		ChatServer.executor.execute(() -> {

			// check if message is not empty
			if ("".contentEquals(message)) {
				return;
			}

			// broadcast message
			for (final ChatClient c : ChatServer.clients) {
				c.receive(name, message);
			}
		});

	}

	static void deregister(ChatClient client) {
		ChatServer.executor.execute(() -> {

			// remove client
			ChatServer.clients.remove(client);

			// inform others if token was present
			for (final ChatClient c : ChatServer.clients) {
				c.left(client.getName());
			}
		});

	}

	static Set<String> getNames() {
		final Set<String> names = new HashSet<>();
		for (ChatClient cc : clients) {
			names.add(cc.getName());
		}

		return names;
	}

	static void register(ChatClient client) {
		ChatServer.executor.execute(() -> {

			clients.add(client);
			for (final ChatClient c : ChatServer.clients) {
				c.joined(client.getName());
			}
		});
	}
}
