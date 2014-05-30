package sneerteam.android.chat;

import static sneerteam.snapi.CloudPath.*;
import rx.Observable;
import rx.functions.*;
import rx.subjects.*;
import sneerteam.snapi.*;
import us.bpsm.edn.*;
import android.app.Application;

public class ChatApp extends Application {
	
	private Cloud cloud;
	private ReplaySubject<Room> rooms = ReplaySubject.create();
	private PublishSubject<String> oneOnOnePublicKeys = PublishSubject.create();

	@Override
	public void onCreate() {
		super.onCreate();
		cloud = Cloud.cloudFor(this);

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
	
	public Observable<Room> rooms() {
		return rooms;
	}

	public Observable<Room> room(final String publicKey) {
		oneOnOnePublicKeys.onNext(publicKey);
		return rooms.filter(new Func1<Room, Boolean>() {@Override public Boolean call(Room room) {
			return publicKey.equals(room.contact().getPublicKey());
		}}).first();
		

	}

}
