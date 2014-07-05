package sneer.chat.simulator;

import rx.*;
import rx.subjects.*;
import sneer.chat.*;
import sneer.snapi.*;

public class RoomSimulator implements Room {

	private Contact contact;
	private ChatGroup group;
	private final ReplaySubject<Message> messages = ReplaySubject.create();
	private boolean isGroup;

	public RoomSimulator(Contact contact) {
		this.contact = contact;
		messages.onNext(new Message(System.currentTimeMillis(), this.contact
				.nickname(), "hello there!"));
	}

	public RoomSimulator(ChatGroup group) {
		this.group = group;
		this.contact = group.contacts.get(1);
		this.isGroup = true;
		messages.onNext(new Message(System.currentTimeMillis(), this.contact
				.nickname(), "hello folks!"));
	}

	@Override
	public int compareTo(Room another) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Contact contact() {
		return contact;
	}

	public ChatGroup group(){
		return group;
	}
	
	@Override
	public String contactPublicKey() {
		return contact.publicKey();
	}

	@Override
	public Observable<Message> messages() {
		return messages;
	}

	@Override
	public void sendMessage(long timestamp, String message) {
		messages.onNext(new Message(System.currentTimeMillis(), message));
	}

	@Override
	public long lastMessageTimestamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		return contact.nickname();
	}

	@Override
	public boolean isGroup() {
		
		return isGroup;
	}

}
