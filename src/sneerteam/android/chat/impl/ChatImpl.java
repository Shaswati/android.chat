package sneerteam.android.chat.impl;
import static sneerteam.snapi.CloudPath.*;

import rx.*;
import rx.functions.*;
import rx.subjects.*;
import sneerteam.android.chat.*;
import sneerteam.android.chat.Contact;
import sneerteam.snapi.*;
import android.content.*;

public class ChatImpl implements Chat {
	
	private final ReplaySubject<Room> rooms = ReplaySubject.create();
	private final PublishSubject<String> oneOnOnePublicKeys = PublishSubject.create();

	public ChatImpl(Context context){
		final Cloud cloud = Cloud.cloudFor(context);

		oneOnOnePublicKeys.distinct()
			.flatMap(new Func1<String, Observable<Room>>() {@Override public Observable<Room> call(final String pk) {
				return cloud.path(ME, "contacts", pk, "nickname").value().map(new Func1<Object, Room>() {@Override public Room call(Object nickname) {
            		Contact contact = new Contact((String) pk, (String) nickname);
					return new Room(cloud, contact);
				}});
			}}).subscribe(rooms);
		
		cloud.path(ME, "contacts").children().flatMap(new Func1<PathEvent, Observable<PathEvent>>() {@Override public Observable<PathEvent> call(PathEvent pk) {
			return cloud.path(pk.path().lastSegment(), "chat", "one-on-one", ME).children().first();
		}}).map(new Func1<PathEvent, String>() {@Override public String call(PathEvent pk) {
			return (String) pk.path().segments().get(0);
		}}).subscribe(oneOnOnePublicKeys);
		
		cloud.path(ME, "chat", "one-on-one").children().map(new Func1<PathEvent, String>() {@Override public String call(PathEvent pk) {
			return (String) pk.path().lastSegment();
		}}).subscribe(oneOnOnePublicKeys);
	}

	@Override
	public Observable<Room> rooms() {
		return rooms;
	}

	@Override
	public Observable<Room> room(final String publicKey) {
		oneOnOnePublicKeys.onNext(publicKey);
		return rooms.filter(new Func1<Room, Boolean>() {@Override public Boolean call(Room room) {
			return publicKey.equals(room.contact().getPublicKey());
		}}).first();
		

	}

}
