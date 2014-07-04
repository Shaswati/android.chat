package sneer.chat;

import rx.*;
import sneer.snapi.*;

public interface Chat {

	Observable<Contact> pickContact();
	Observable<ChatGroup> pickGroup();
	
	Observable<Room> rooms();
	Room produceRoomWith(Contact contact);
	Room produceRoomWith(ChatGroup group);
	Room findRoom(String contactPuk);
	

}
