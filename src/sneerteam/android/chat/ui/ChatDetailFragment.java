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
import android.view.*;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;

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
	private List<Message> messages = createInitialMessages();
	private Contact contact;
	private Cloud cloud;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ChatDetailFragment() {
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

	
	private static List<Message> createInitialMessages() {
		List<Message> messages = new ArrayList<Message>();
		messages.add(new Message(0, "Sneer", "Welcome to chat room. Be awesome."));
		return messages;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chat_detail, container, false);

		contact = (Contact) getArguments().getParcelable("contact");

		this.getActivity().setTitle(contact.getNickname());
		
		cloud = Cloud.cloudFor(this.getActivity());
		
		chatAdapter = new ChatAdapter(this.getActivity(), inflater, R.layout.list_item_user_message, R.layout.list_item_contact_message, messages);
		chatAdapter.setSender("me");

		ListView listView = (ListView) rootView.findViewById(R.id.listView);
		listView.setAdapter(chatAdapter);
		
		listenOn(cloud.path(":me", "chat", "private", contact.getPublicKey()), "me");
		listenOn(cloud.path(contact.getPublicKey(), "chat", "private", ":me"), contact.getNickname());

		final TextView widget = (TextView)rootView.findViewById(R.id.editText);
		
		widget.setOnEditorActionListener(new OnEditorActionListener() {@Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				sendText(widget);
			}
			return false;
		}});
		
		Button b =  (Button) rootView.findViewById(R.id.sendButton);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {
			sendText(widget);
		}});

		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		cloud.dispose();
		super.onDestroyView();
	}

	private void sendText(final TextView widget) {
		cloud.path("chat", "private", contact.getPublicKey(), System.currentTimeMillis()).pub(widget.getText().toString());
		widget.setText("");
	}

}
