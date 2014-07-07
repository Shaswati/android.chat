package sneer.chat.impl_old;

import static sneer.snapi.CloudPath.*;
import rx.*;
import rx.functions.*;
import rx.subjects.*;
import sneer.chat.*;
import sneer.chat.util.*;
import sneer.snapi.*;

public class RoomImpl implements OldRoom {
	
	private Contact contact;
	private Subject<OldMessage, OldMessage> messages = ReplaySubject.create();
	private Cloud cloud;
	private long lastMessageTimestamp = System.currentTimeMillis();
	
	public RoomImpl(Cloud cloud, Contact contact) {
		this.cloud = cloud;
		this.contact = contact;
		listenOn(cloud.path(ME, "chat", "one-on-one", contact.publicKey()), "me").subscribe(messages);
		listenOn(cloud.path(contact.publicKey(), "chat", "one-on-one", ME), contact.nickname()).subscribe(messages);
		messages.subscribe(new Action1<OldMessage>() {@Override public void call(OldMessage msg) {
			lastMessageTimestamp = Math.max(msg.timestamp(), lastMessageTimestamp);
		}});
	}

    @Override
	public int compareTo(OldRoom another) {
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
		return contact.nickname();
	}

	@Override
	public Observable<OldMessage> messages() {
		return messages;
	}

	private Observable<OldMessage> listenOn(CloudPath path, final String sender) {
		return path.children()
			.flatMap(new Func1<PathEvent, Observable<? extends OldMessage>>() {@Override public Observable<? extends OldMessage> call(final PathEvent path) {
				return path.path().value()
				.map(new Func1<Object, OldMessage>() {@Override public OldMessage call(Object message) {
					return new OldMessage((Long) path.path().lastSegment(), sender, (String)message);
				}});
			}});
	}
	
	@Override
	public void sendMessage(long timestamp, String message) {
		cloud.path("chat", "one-on-one", contact.publicKey(), timestamp).pub(message);
	}

	@Override
	public Contact contact() {
		return contact;
	}

	@Override
	public String contactPublicKey() {
		return contact.publicKey();
	}

	@Override
	public boolean isGroup() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
