package app.chat;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.vaadin.flow.shared.Registration;

public class Chatter {

	static Executor executor = Executors.newSingleThreadExecutor();

	static LinkedList<Consumer<Message>> listeners = new LinkedList<>();

	public static synchronized Registration register(Consumer<Message> listener) {
		listeners.add(listener);

		return () -> {
			synchronized (Chatter.class) {
				listeners.remove(listener);
			}
		};
	}

	public static synchronized void send(Message message) {
		for (Consumer<Message> listener : listeners) {
			executor.execute(() -> listener.accept(message));
		}
	}
}
