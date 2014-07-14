package sneer.chat.simulator;

import java.util.*;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.*;
import sneer.chat.*;

public class ChatSimulator implements Chat {
	
	private final ReplaySubject<Party> parties = ReplaySubject.create();
	private final ReplaySubject<Conversation> conversations = ReplaySubject.create();
	private final Map<Party, Conversation> conversationsByParty = new HashMap<Party, Conversation>();

	
	@Override
	public Observable<Party> parties() {
		return parties;
	}

	
	@Override
	public Party findParty(final String publicKey) {
		return first(parties.filter(new Func1<Party, Boolean>() { @Override public Boolean call(Party party) {
			return first(party.publicKey()).equals(publicKey);
		}}));
		
		//return findIndividualParty();
	}


	@Override
	public Observable<Conversation> conversations() {
		return conversations;
	}

	
	@Override
	public Conversation produceConversationWith(Party party) {
		Conversation ret = conversationsByParty.get(party);
		if (ret == null) {
			ret = new ConversationSimulator(party);
			conversationsByParty.put(party, ret);
			conversations.onNext(ret);
		}
		return ret;
	}
	
	
	static private <T> T first(Observable<T> observable) {
		return observable.toBlockingObservable().first();
	}

	private Party findIndividualParty(){
		//public IndividualSimulator(Observable<String> publicKey, Observable<String> nickname,Observable<String> name) {
		return new IndividualSimulator(Observable.from("P00001"), Observable.from("Segunda-feira"), Observable.from("Quebra Pernas") );
	}
	
}
