package sneer.chat.impl;

import static sneerteam.snapi.CloudPath.*;
import rx.Observable;
import rx.functions.*;
import rx.subjects.*;
import sneer.chat.*;
import sneer.chat.Contact;
import sneer.chat.util.*;
import sneerteam.snapi.*;

public class RoomImpl implements Room {
	
	private Contact contact;
	private Subject<Message, Message> messages = ReplaySubject.create();
	private Cloud cloud;
	private long lastMessageTimestamp = System.currentTimeMillis();
	
	public RoomImpl(Cloud cloud, Contact contact) {
		this.cloud = cloud;
		this.contact = contact;
		listenOn(cloud.path(ME, "chat", "one-on-one", contact.getPublicKey()), "me").subscribe(messages);
		listenOn(cloud.path(contact.getPublicKey(), "chat", "one-on-one", ME), contact.getNickname()).subscribe(messages);
		messages.subscribe(new Action1<Message>() {@Override public void call(Message msg) {
			lastMessageTimestamp = Math.max(msg.timestamp(), lastMessageTimestamp);
		}});
	}

    @Override
	public int compareTo(Room another) {
		return Comparators.compare(another.lastMessageTimestamp(), lastMessageTimestamp());
	}

    @Override
	public long lastMessageTimestamp() {	
		return lastMessageTimestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoomImpl other = (RoomImpl) obj;
		if (contact == null) {
			if (other.contact != null)
				return false;
		} else if (!contact.equals(other.contact))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return contact.getNickname();
	}

	@Override
	public Observable<Message> messages() {
		return messages;
	}

	private Observable<Message> listenOn(CloudPath path, final String sender) {
		return path.children()
			.flatMap(new Func1<PathEvent, Observable<? extends Message>>() {@Override public Observable<? extends Message> call(final PathEvent path) {
				return path.path().value()
				.map(new Func1<Object, Message>() {@Override public Message call(Object message) {
					return new Message((Long) path.path().lastSegment(), sender, (String)message);
				}});
			}});
	}
	
	@Override
	public void sendMessage(long timestamp, String message) {
		cloud.path("chat", "one-on-one", contact.getPublicKey(), timestamp).pub(message);
	}

	@Override
	public Contact contact() {
		return contact;
	}

	@Override
	public String publicKey() {
		return contact.getPublicKey();
	}
	
}
