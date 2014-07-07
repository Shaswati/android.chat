package sneer.chat;


public class Message {
	
	private final String content;
	private final Party sender;
	
	private final long timestamp;
	
	
	public Message(long timestamp, String content) {
		this.timestamp = timestamp;
		this.sender = null;
		this.content = content;
	}
	
	
	public Message(long timestamp, Party sender, String content) {
		this.timestamp = timestamp;
		this.sender = sender;
		this.content = content;
	}

	
	public boolean isOwn() {
		return sender == null;
	}
	
	
	public Party sender() {
		return sender;
	}
	
	
	public String content() {
		return content;
	}
	
	
	public long timestamp() {
		return timestamp;
	}


	@Override
	public String toString() {
		return "Message [" + timestamp + " " + sender + ": " + content + "]";
	}	
	
}
