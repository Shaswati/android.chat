package sneer.chat.simulator;

import java.util.*;

import rx.Observable;
import rx.subjects.*;
import sneer.chat.*;
import sneerteam.snapi.*;

public class ChatSimulator implements Chat {

	private final ReplaySubject<Room> rooms = ReplaySubject.create();
	private final Map<String, RoomSimulator> roomsByPuk = new HashMap<String, RoomSimulator>();

	@Override
	public Observable<Room> rooms() {
		return rooms;
	}

	@Override
	public Room produceRoomWith(Contact contact) {
		RoomSimulator ret = roomsByPuk.get(contact.publicKey());
		if (ret == null) {
			ret = new RoomSimulator(contact);
			roomsByPuk.put(contact.publicKey(), ret);
			rooms.onNext(ret);
		}
		return ret;
	}

	@Override
	public Room findRoom(String contactPuk) {
		RoomSimulator ret = roomsByPuk.get(contactPuk);
		if (ret == null) 
			throw new IllegalArgumentException("No room found with publicKey " + contactPuk);
		
		return ret;
	}

	@Override
	public Observable<Contact> pickContact() {
		return Observable.from(new Contact("fdsfs098", "Neide"));
	}	

}
