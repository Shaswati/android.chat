package sneer.chat;

import rx.Observable;

public interface Conversation {

	Party party();
	
	Party party(Party party);
	
	Observable<Message> messages();
	
	/** Publish a new Message with isOwn() true, with party() as the audience, with the received content and using System.currentTimeMillis() as the timestamp. */
	void sendMessage(String content);
	
	long lastMessageTimestamp();
	
}
