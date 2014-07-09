package sneer.chat.simulator;

import rx.Observable;
import sneer.chat.Group;
import sneer.chat.Party;

public class GroupSimulator implements Group {

	private Observable<Party> members;
	
	public GroupSimulator(){
		
	}
	
	@Override
	public Observable<String> publicKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observable<String> nickname() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observable<String> name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observable<Party> members() {
		return members;
	}

}
