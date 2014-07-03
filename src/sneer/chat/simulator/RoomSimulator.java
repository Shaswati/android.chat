package sneer.chat.simulator;

import rx.*;
import rx.subjects.*;
import sneer.chat.*;
import sneer.snapi.*;

public class RoomSimulator implements Room {

	private Contact contact;
	private final ReplaySubject<Message> messages = ReplaySubject.create();

	public RoomSimulator(Contact contact) {
		this.contact = contact;
		messages.onNext(new Message(System.currentTimeMillis(), contact.nickname(), "hello there!"));
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

	@Override
	public String publicKey() {
		return contact.publicKey();
	}

	@Override
	public Observable<Message> messages() {
		return messages ;
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

}
