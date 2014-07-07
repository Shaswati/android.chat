package sneer.chat.simulator;

import rx.*;
import rx.subjects.*;
import sneer.chat.*;

public class PartySimulator implements Party {
	
	private final ReplaySubject<Message> messages = ReplaySubject.create();
	
	private Party party;

	public PartySimulator(Party party) {
		this.party = party;
		messages.onNext(new Message(System.currentTimeMillis(), this.party, "q festa!!!! uhuu!!!"));
	}

	@Override
	public Observable<String> publicKey() {
		return party.publicKey();
	}

	@Override
	public Observable<String> nickname() {
		return party.nickname();
	}

	@Override
	public Observable<String> name() {
		return party.name();
	}

}
