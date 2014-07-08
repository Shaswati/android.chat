package sneer.chat.simulator;

import rx.Observable;
import sneer.chat.Party;

public class PartySimulator implements Party {
	
	
	private Party party;

	public PartySimulator(Party party) {
		this.party = party;
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
