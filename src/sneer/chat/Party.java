package sneer.chat;

import rx.Observable;

/** An individual or a group. See type hierarchy. */
public interface Party {

	Observable<String> publicKey();

	/**	The nickname you give to a party. */
	Observable<String> nickname();

	/** The name this Party gives itself.
	 * JUST AN EXAMPLE FOR THE FUTURE. NOT IMPLEMENTED OR USED IN THE CHAT APP. */
	Observable<String> name();
	
}
