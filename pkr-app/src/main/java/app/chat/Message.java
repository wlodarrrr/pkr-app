package app.chat;

public class Message {

	private String sender;
	private String message;

	public Message(String sender, String message) {
		super();
		this.sender = sender;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String getSender() {
		return sender;
	}
}
