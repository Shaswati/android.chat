package sneer.chat.impl_old;
import static sneer.snapi.CloudPath.*;
import rx.*;
import rx.functions.*;
import rx.subjects.*;
import sneer.chat.*;
import sneer.snapi.*;
import android.content.*;

public class ChatImpl implements OldChat {
	
	private final Context context;
	private final ReplaySubject<OldRoom> rooms = ReplaySubject.create();
	private final PublishSubject<String> oneOnOnePublicKeys = PublishSubject.create();

	public ChatImpl(Context context) {
		this.context = context;
		final Cloud cloud = Cloud.cloudFor(this.context);

		oneOnOnePublicKeys.distinct()
			.flatMap(new Func1<String, Observable<RoomImpl>>() {@Override public Observable<RoomImpl> call(final String pk) {
				return cloud.path(ME, "contacts", pk, "nickname").value().map(new Func1<Object, RoomImpl>() {@Override public RoomImpl call(Object nickname) {
            		Contact contact = new Contact((String) pk, (String) nickname);
					return new RoomImpl(cloud, contact);
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
	public Observable<OldRoom> rooms() {
		return rooms;
	}

	@Override
	public OldRoom produceRoomWith(final Contact contact) {
//		oneOnOnePublicKeys.onNext(publicKey);
//		return rooms.filter(new Func1<Room, Boolean>() {@Override public Boolean call(Room room) {
//			return publicKey.equals(room.publicKey());
//		}}).first();
		
		
		// Creates a new Room or returns an existing one
		return null;
	}

	@Override
	public OldRoom findRoom(String contactPuk) {
		// Throws an exception if the Room is not found
		return null;
	}

	@Override
	public Observable<Contact> pickContact() {
		return ContactPicker.pickContact(context);
	}

	@Override
	public OldRoom produceRoomWith(OldGroup group) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Observable<OldGroup> pickGroup() {
		// TODO Auto-generated method stub
		return null;
	}

}
