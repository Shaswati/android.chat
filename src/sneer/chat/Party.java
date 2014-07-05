package sneer.chat;

import rx.Observable;

/** An individual or a group. See hierarchy. */
public interface Party {

	Observable<String> publicKey();

	/**	The nickname you give to a party. */
	Observable<String> nickname();

	/** The name this Party gives itself. */
	Observable<String> name();
	
}
