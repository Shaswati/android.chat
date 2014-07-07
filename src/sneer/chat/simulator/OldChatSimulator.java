//package sneer.chat.simulator;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import rx.Observable;
//import rx.subjects.ReplaySubject;
//import sneer.chat.OldChat;
//import sneer.chat.OldGroup;
//import sneer.chat.OldRoom;
//import sneer.snapi.Contact;
//
//public class OldChatSimulator implements OldChat {
//
//	private final ReplaySubject<OldRoom> rooms = ReplaySubject.create();
//	private final Map<String, PartySimulator> roomsByPuk = new HashMap<String, PartySimulator>();
//
//	@Override
//	public Observable<OldRoom> rooms() {
//		return rooms;
//	}
//
//	@Override
//	public OldRoom produceRoomWith(Contact contact) {
//		PartySimulator ret = roomsByPuk.get(contact.publicKey());
//		if (ret == null) {
//			ret = new PartySimulator(contact);
//			roomsByPuk.put(contact.publicKey(), ret);
//			rooms.onNext(ret);
//		}
//		return ret;
//	}
//
//	@Override
//	public OldRoom produceRoomWith(OldGroup group) {
//		
//		PartySimulator ret = roomsByPuk.get(group.contacts.get(1).publicKey());
//		if (ret == null) {
//			ret = new PartySimulator(group);
//			roomsByPuk.put(group.contacts.get(1).publicKey(), ret);
//			rooms.onNext(ret);
//		}
//		return ret;
//	}
//	
//	@Override
//	public OldRoom findRoom(String contactPuk) {
//		PartySimulator ret = roomsByPuk.get(contactPuk);
//		if (ret == null) 
//			throw new IllegalArgumentException("No room found with publicKey " + contactPuk);
//		
//		return ret;
//	}
//
//	@Override
//	public Observable<Contact> pickContact() {
//		return Observable.from(new Contact("fdsfs098", "Neide"));
//	}
//
//	public Observable<OldGroup> pickGroup(){
//		List<Contact> contacts = new ArrayList<Contact>();
//		contacts.add(new Contact("fdsfs098", "Neide"));
//		contacts.add(new Contact("fdsfs097", "Pedro"));
//		contacts.add(new Contact("fdsfs096", "Alberto"));
//		contacts.add(new Contact("fdsfs095", "Batman"));
//		contacts.add(new Contact("fdsfs094", "Lanterna Verde"));
//		
//		return Observable.from(new OldGroup(contacts));
//
//		
//	}
//
//}
