package sneer.chat.simulator;

import rx.Observable;
import sneer.chat.Group;
import sneer.chat.Party;

public class GroupSimulator implements Group {

	private Observable<Party> members;
	private Observable<String> name;
	private Observable<String> nickname;
	private Observable<String> publicKey;
	
	public GroupSimulator(){
		
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

	@Override
	public Observable<Party> members() {
		return members;
	}

}
