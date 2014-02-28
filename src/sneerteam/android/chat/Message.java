package sneerteam.android.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Comparable<Message> {
	
	private String content;
	private String sender;
	
	private long timestamp;
	
	public Message(long timestamp, String sender, String content) {
		this.timestamp = timestamp;
		this.sender = sender;
		this.content = content;
	}

	public String sender() {
		return sender;
	}

	@Override public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM HH:mm");
		String datestr = sdf.format(new Date(timestamp));
		return datestr + " " + sender + ": " + content;
	}

	@Override
	public int compareTo(Message another) {
		if (timestamp < another.timestamp)
			return -1;
		if (timestamp > another.timestamp)
			return +1;
		int val = sender.compareTo(another.sender);
		if (val != 0)
			return val;
		return content.compareTo(another.content);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (sender == null) {
			if (other.sender != null)
				return false;
		} else if (!sender.equals(other.sender))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}
	
	
}
