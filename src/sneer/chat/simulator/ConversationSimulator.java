package sneer.chat.simulator;

import rx.*;
import sneer.chat.*;

public class ConversationSimulator implements Conversation {
	
	private Party party;
	private String content;

	public ConversationSimulator(Party party) {
		this.party = party;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Party party() {
		return this.party;
	}

	@Override
	public Observable<Message> messages() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMessage(String content) {
		this.content = content;
		
	}

}
