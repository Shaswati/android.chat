package sneer.chat;

import rx.*;
import sneer.snapi.*;

public interface OldChat {

	Observable<Contact> pickContact();
	Observable<OldGroup> pickGroup();
	
	Observable<OldRoom> rooms();
	OldRoom produceRoomWith(Contact contact);
	OldRoom produceRoomWith(OldGroup group);
	OldRoom findRoom(String contactPuk);

}
