package sneer.chat;

import rx.*;
import sneer.snapi.*;

public interface Room extends Comparable<Room> {

	String contactPublicKey();
	Contact contact();
	boolean isGroup();
	
	Observable<Message> messages();
	void sendMessage(long timestamp, String message);
	long lastMessageTimestamp();

	
}
