package app.chat;

public class Message {

	public static enum Type {
		MSG, SYSTEM
	}

	private String sender;
	private String message;
	private Type type;

	public Message(String sender, String message, Type type) {
		super();
		this.sender = sender;
		this.message = message;
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public String getSender() {
		return sender;
	}

	public Type getType() {
		return type;
	}
}
