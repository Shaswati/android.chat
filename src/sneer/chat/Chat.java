package sneer.chat;

import rx.Observable;

public interface Chat {

	/** All Conversations you have had. */
	Observable<Conversation> conversations();

	/** All Individual contacts that you have and all Groups you are a member of. */
	Observable<Party> parties();
	Observable<Party> findParty(String publicKey);

	/** @return a new or existing Conversation with party. */
	Conversation produceConversationWith(Party party);

}
