package sneer.chat;

import rx.*;

public interface Room extends Comparable<Room> {

	Contact contact();
	/** @return contact().publicKey() */
	String publicKey();

	Observable<Message> messages();
	void sendMessage(long timestamp, String message);
	long lastMessageTimestamp();

}
