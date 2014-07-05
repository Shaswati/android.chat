package sneer.chat;

import rx.Observable;


public interface Group extends Party {
	
	Observable<Party> members();
	
}
