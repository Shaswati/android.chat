package sneer.chat;

import rx.*;
import sneer.snapi.*;

public interface Room extends Comparable<Room> {

	Contact contact();
	/** @return contact().publicKey() */
	String publicKey();
	boolean isGroup();
	
	Observable<Message> messages();
	void sendMessage(long timestamp, String message);
	long lastMessageTimestamp();

	
}
