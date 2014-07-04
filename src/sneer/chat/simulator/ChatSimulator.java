package sneer.chat.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.subjects.ReplaySubject;
import sneer.chat.Chat;
import sneer.chat.ChatGroup;
import sneer.chat.Room;
import sneer.snapi.Contact;

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
	public Room produceRoomWith(ChatGroup group) {
		
		RoomSimulator ret = roomsByPuk.get(group.contacts.get(1).publicKey());
		if (ret == null) {
			ret = new RoomSimulator(group);
			roomsByPuk.put(group.contacts.get(1).publicKey(), ret);
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

	public Observable<ChatGroup> pickGroup(){
		List<Contact> contacts = new ArrayList<Contact>();
		contacts.add(new Contact("fdsfs098", "Neide"));
		contacts.add(new Contact("fdsfs097", "Pedro"));
		contacts.add(new Contact("fdsfs096", "Alberto"));
		contacts.add(new Contact("fdsfs095", "Batman"));
		contacts.add(new Contact("fdsfs094", "Lanterna Verde"));
		
		return Observable.from(new ChatGroup(contacts));

		
	}

}
