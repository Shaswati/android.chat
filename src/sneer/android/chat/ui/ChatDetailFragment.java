package sneer.android.chat.ui;

import java.util.*;

import rx.android.schedulers.*;
import rx.functions.*;
import rx.subjects.*;
import sneer.android.chat.*;
import sneer.chat.*;
import sneer.chat.OldMessage;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

/**
 * A fragment representing a single Chat detail screen. This fragment is either
 * contained in a {@link ChatListActivity} in two-pane mode (on tablets) or a
 * {@link ChatDetailActivity} on handsets.
 */
public class ChatDetailFragment extends Fragment {
	static final String CONTACT_PUK = "contactPuk";
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	private ChatAdapter chatAdapter;
	private List<OldMessage> messages = createInitialMessages();
	private ReplaySubject<Pair<Long, String>> sendSubject = ReplaySubject
			.create();

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ChatDetailFragment() {
	}

	protected void onMessage(OldMessage msg) {
		int insertionPointHint = Collections.binarySearch(messages, msg);
		if (insertionPointHint < 0) {
			int insertionPoint = Math.abs(insertionPointHint) - 1;
			messages.add(insertionPoint, msg);
			chatAdapter.notifyDataSetChanged();
		}
	}

	private static List<OldMessage> createInitialMessages() {
		List<OldMessage> messages = new ArrayList<OldMessage>();
		return messages;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chat_detail,
				container, false);

		OldChat chat = ((ChatApp) getActivity().getApplication()).model();
		final OldRoom room = chat.findRoom(getArguments().getString(CONTACT_PUK));

		if (room.isGroup())
			getActivity().setTitle("Chat Group");
		else
			getActivity().setTitle(room.contact().nickname());

		sendSubject.subscribe(new Action1<Pair<Long, String>>() {
			@Override
			public void call(Pair<Long, String> msg) {
				room.sendMessage(msg.first, msg.second);
			}
		});

		room.messages().observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<OldMessage>() {
					@Override
					public void call(OldMessage msg) {
						onMessage(msg);
					}
				});

		chatAdapter = new ChatAdapter(this.getActivity(), inflater,
				R.layout.list_item_user_message,
				R.layout.list_item_contact_message, messages);

		ListView listView = (ListView) rootView.findViewById(R.id.listView);
		listView.setAdapter(chatAdapter);

		final TextView widget = (TextView) rootView.findViewById(R.id.editText);

		// widget.setOnEditorActionListener(new OnEditorActionListener()
		// {@Override public boolean onEditorAction(TextView v, int actionId,
		// KeyEvent event) {
		// if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
		// sendText(widget);
		// }
		// return false;
		// }});

		Button b = (Button) rootView.findViewById(R.id.sendButton);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendText(widget);
			}
		});

		return rootView;
	}

	private void sendText(final TextView widget) {
		sendSubject.onNext(Pair.create(System.currentTimeMillis(), widget
				.getText().toString()));
		widget.setText("");
	}

}
