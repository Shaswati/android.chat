package sneer.chat;

import rx.*;
import sneer.snapi.*;

public interface OldRoom extends Comparable<OldRoom> {

	String contactPublicKey();
	Contact contact();
	boolean isGroup();
	
	Observable<OldMessage> messages();
	void sendMessage(long timestamp, String message);
	long lastMessageTimestamp();

	
}
