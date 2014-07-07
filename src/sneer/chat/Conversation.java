package sneer.chat;

import rx.Observable;

public interface Conversation extends Comparable<Conversation> {

	Party party();
	
	Observable<OldMessage> messages();
	void sendMessage(long timestamp, String message);
	
}
