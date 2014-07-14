package sneer.chat.simulator;

import rx.*;
import rx.subjects.ReplaySubject;
import sneer.chat.*;

public class ConversationSimulator implements Conversation {

	private final ReplaySubject<Message> messages = ReplaySubject.create();

	private Party party;

	
	public ConversationSimulator(Party party) {
		this.party = party;
		sendMessage("q festa!!!! uhuu!!!");
		messages.onNext(new Message(now(), now(), this.party, "Onde? Onde??"));
	}

	
	@Override
	public Party party() {
		return party;
	}

	
	@Override
	public Observable<Message> messages() {
		return messages;
	}

	
	@Override
	public void sendMessage(String content) {
		messages.onNext(new Message(now(), 0, party, content));
	}
	
	
	static private long now() {
		return System.currentTimeMillis();
	}


	@Override
	public long lastMessageTimestamp() {
		return 0;
	}


	@Override
	public Party party(Party party) {
		// TODO Auto-generated method stub
		return null;
	}

}
