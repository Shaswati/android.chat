package sneer.android.chat;

import rx.*;

public interface Chat {

	Observable<Room> rooms();

	Observable<Room> room(String publicKey);

}
