package sneer.chat;

import rx.Observable;

/** An individual or a group. */
public interface Party {

	Observable<String> publicKey();

	/**	The nickname you give to a party. */
	Observable<String> nickname();
	
	/**	The name a party gives itself. */
	Observable<String> name();
	
}
