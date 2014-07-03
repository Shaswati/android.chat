package sneer.chat;

import rx.*;
import sneerteam.snapi.*;

public interface Chat {

	Observable<Contact> pickContact();

	Observable<Room> rooms();
	Room produceRoomWith(Contact contact);
	Room findRoom(String contactPuk);

}
