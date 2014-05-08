package sneerteam.android.chat.ui;

import java.util.*;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.*;
import sneerteam.android.chat.*;
import sneerteam.android.chat.R;
import sneerteam.snapi.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;

/**
 * A fragment representing a single Chat detail screen. This fragment is either
 * contained in a {@link ChatListActivity} in two-pane mode (on tablets) or a
 * {@link ChatDetailActivity} on handsets.
 */
public class ChatDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	private ChatAdapter chatAdapter;
	private Contact contact;
	private List<Message> messages = createInitialMessages();
	private Cloud cloud;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ChatDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contact = (Contact) getArguments().getSerializable("contact");
		this.getActivity().setTitle(contact.getNickname());
		
		chatAdapter = new ChatAdapter(this.getActivity(), R.layout.list_item_user_message, R.layout.list_item_contact_message, messages);
		chatAdapter.setSender("me");

		ListView listView = (ListView) this.getActivity().findViewById(R.id.listView);
		listView.setAdapter(chatAdapter);
		
		cloud = Cloud.cloudFor(getActivity());
		
		listenOn(cloud.path(":me", "contacts", contact.getPublicKey(), "chat"), "me");
		listenOn(cloud.path(contact.getPublicKey(), "contacts", ":me", "chat"), contact.getNickname());
	}

	private void listenOn(CloudPath path, final String sender) {
		path.children()
			.flatMap(new Func1<PathEvent, Observable<? extends Message>>() {@Override public Observable<? extends Message> call(final PathEvent path) {
				return path.path().value().map(new Func1<Object, Message>() {@Override public Message call(Object message) {
					return new Message((Long) path.path().lastSegment(), sender, (String)message);
				}});
			}}).observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Action1<Message>() {@Override public void call(Message msg) {
				onMessage(msg);
			}});
	}
	
	protected void onMessage(Message msg) {
		int insertionPointHint = Collections.binarySearch(messages, msg);
		if (insertionPointHint < 0) {
			int insertionPoint = Math.abs(insertionPointHint) - 1;
			messages.add(insertionPoint, msg);
			chatAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		cloud.dispose();
		super.onDestroy();
	}
	
	private static List<Message> createInitialMessages() {
		List<Message> messages = new ArrayList<Message>();
		messages.add(new Message(0, "Sneer", "Welcome to chat room. Be awesome."));
		return messages;
	}
}
