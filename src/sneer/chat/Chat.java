package sneer.chat;

import rx.Observable;

public interface Chat {

	/** All Individual contacts that you have and all Groups you are a member of. */
	Observable<Party> parties();
	Observable<Party> findParty(String publicKey);

	/** All Conversations you have had. */
	Observable<Conversation> conversations();

	/** @return an existing Conversation with party or a new one if it doesn't exist. */
	Conversation produceConversationWith(Party party);

}
