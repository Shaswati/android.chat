package sneer.chat.simulator;

import java.util.*;

import rx.Observable;
import rx.subjects.*;
import sneer.chat.*;

public class ChatSimulator implements Chat {
	
	private final ReplaySubject<Conversation> conversations = ReplaySubject.create();
	private final Map<Observable<String>, ConversationSimulator> conversationsByPuk = new HashMap<Observable<String>, ConversationSimulator>();

	@Override
	public Observable<Party> parties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observable<Party> findParty(String publicKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observable<Conversation> conversations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Conversation produceConversationWith(Party party) {
		if (party instanceof Individual)
			return produceConversationWithIndividual(party);
		else
			return produceConversationWithGroup(party);
	}
	
	private Conversation produceConversationWithIndividual(Party party) {
		ConversationSimulator ret = conversationsByPuk.get(party.publicKey());
		if (ret == null) {
			ret = new ConversationSimulator(party);
			conversationsByPuk.put(party.publicKey(), ret);
			conversations.onNext(ret);
		}
		return ret;
	}

	private Conversation produceConversationWithGroup(Party party) {
		return null;
	}

	@Override
	public Conversation findConversation(String publicKey) {
		ConversationSimulator ret = conversationsByPuk.get(publicKey);
		if (ret == null) 
			throw new IllegalArgumentException("No conversation found with publicKey " + publicKey);
		
		return ret;
	}

	@Override
	public Observable<Party> pickParty() {
		return Observable.from(new Party("fdsfs098", "Neide"));
	}

}
