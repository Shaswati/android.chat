package sneer.chat.simulator;

import rx.Observable;
import sneer.chat.Individual;

public class IndividualSimulator implements Individual {

	private Observable<String> publicKey;
	private Observable<String> nickname;
	private Observable<String> name;

	public IndividualSimulator(Observable<String> publicKey, Observable<String> nickname,
			Observable<String> name) {
		this.publicKey = publicKey;
		this.nickname = nickname;
		this.name = name;
	}

	@Override
	public Observable<String> publicKey() {
		return publicKey;
	}

	@Override
	public Observable<String> nickname() {
		return nickname;
	}

	@Override
	public Observable<String> name() {
		return name;
	}

}
