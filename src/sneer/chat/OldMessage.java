package sneer.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class OldMessage implements Comparable<OldMessage> {
	
	private final String content;
	private final String sender;
	
	private final long timestamp;
	private final boolean isOwn;
	
	public OldMessage(long timestamp, String content) {
		this.timestamp = timestamp;
		this.sender = null;
		this.content = content;
		this.isOwn = true;
	}
	
	public OldMessage(long timestamp, String sender, String content) {
		this.timestamp = timestamp;
		this.sender = sender;
		this.content = content;
		this.isOwn = false;
	}

	public boolean isOwn() {
		return isOwn;
	}
	
	public String sender() {
		return sender;
	}
	
	public String content() {
		return content;
	}
	
	@SuppressLint("SimpleDateFormat")
	public String time() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM HH:mm");
		return sdf.format(new Date(timestamp));
	}
	
	public long timestamp() {
		return timestamp;
	}

	@Override
	public int compareTo(OldMessage another) {
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
		OldMessage other = (OldMessage) obj;
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

	@Override
	public String toString() {
		return "Message [" + timestamp + " " + sender + ": " + content + "]";
	}	
	
}
