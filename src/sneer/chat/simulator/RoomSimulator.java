package sneer.chat.simulator;

import rx.*;
import rx.subjects.*;
import sneer.chat.*;
import sneer.snapi.*;

public class RoomSimulator implements OldRoom {

	private Contact contact;
	private OldGroup group;
	private final ReplaySubject<OldMessage> messages = ReplaySubject.create();
	private boolean isGroup;

	public RoomSimulator(Contact contact) {
		this.contact = contact;
		messages.onNext(new OldMessage(System.currentTimeMillis(), this.contact
				.nickname(), "hello there!"));
	}

	public RoomSimulator(OldGroup group) {
		this.group = group;
		this.contact = group.contacts.get(1);
		this.isGroup = true;
		messages.onNext(new OldMessage(System.currentTimeMillis(), this.contact
				.nickname(), "hello folks!"));
	}

	@Override
	public int compareTo(OldRoom another) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Contact contact() {
		return contact;
	}

	public OldGroup group(){
		return group;
	}
	
	@Override
	public String contactPublicKey() {
		return contact.publicKey();
	}

	@Override
	public Observable<OldMessage> messages() {
		return messages;
	}

	@Override
	public void sendMessage(long timestamp, String message) {
		messages.onNext(new OldMessage(System.currentTimeMillis(), message));
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
