package sneerteam.android.chat;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.*;
import sneerteam.android.chat.ui.Room;
import sneerteam.snapi.*;
import android.app.Application;

public class ChatApp extends Application {
	
	private Cloud cloud;
	private ReplaySubject<Room> rooms = ReplaySubject.create();
	private PublishSubject<String> ids = PublishSubject.create();

	@Override
	public void onCreate() {
		super.onCreate();
		cloud = Cloud.cloudFor(this);

		ids.distinct()
			.flatMap(new Func1<String, Observable<Room>>() {@Override public Observable<Room> call(final String pk) {
				return cloud.path(":me", "contacts", pk, "nickname").value().map(new Func1<Object, Room>() {@Override public Room call(Object nickname) {
            		Contact contact = new Contact((String) pk, (String) nickname);
					return new Room(cloud, contact);
				}});
			}}).subscribe(rooms);
		
		cloud.path(":me", "chat", "one-on-one").children().map(new Func1<PathEvent, String>() {@Override public String call(PathEvent pk) {
			return (String) pk.path().lastSegment();
		}}).subscribe(ids);
		
	}
	
	public Observable<Room> rooms() {
		return rooms;
	}

	public Observable<Room> room(final String publicKey) {
		ids.onNext(publicKey);
		return rooms.filter(new Func1<Room, Boolean>() {@Override public Boolean call(Room room) {
			return publicKey.equals(room.contact().getPublicKey());
		}}).first();
		

	}

}
