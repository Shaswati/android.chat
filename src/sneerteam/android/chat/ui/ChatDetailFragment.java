package sneerteam.android.chat.ui;

import java.util.*;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.ReplaySubject;
import sneerteam.android.chat.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;


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
	private ReplaySubject<Pair<Long, String>> sendSubject = ReplaySubject.create();

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ChatDetailFragment() {
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
		return messages;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chat_detail, container, false);

		Chat app = ((ChatApp) getActivity().getApplication()).model();
		app.room(getArguments().getString("room")).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Room>() {@Override public void call(final Room room) {

			getActivity().setTitle(room.contact().getNickname());
			
			sendSubject.subscribe(new Action1<Pair<Long, String>>() {@Override public void call(Pair<Long, String> msg) {
				room.sendMessage(msg.first, msg.second);
			}});
			
			room.messages().observeOn(AndroidSchedulers.mainThread())
			.subscribe(new Action1<Message>() {@Override public void call(Message msg) {
				onMessage(msg);
			}});
		}});

		
		chatAdapter = new ChatAdapter(this.getActivity(), inflater, R.layout.list_item_user_message, R.layout.list_item_contact_message, messages);
		chatAdapter.setSender("me");

		ListView listView = (ListView) rootView.findViewById(R.id.listView);
		listView.setAdapter(chatAdapter);
		
		final TextView widget = (TextView)rootView.findViewById(R.id.editText);
		
//		widget.setOnEditorActionListener(new OnEditorActionListener() {@Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//			if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//				sendText(widget);
//			}
//			return false;
//		}});
		
		Button b =  (Button) rootView.findViewById(R.id.sendButton);
		b.setOnClickListener(new OnClickListener() {@Override public void onClick(View v) {
			sendText(widget);
		}});

		return rootView;
	}
	
	private void sendText(final TextView widget) {
		sendSubject.onNext(Pair.create(System.currentTimeMillis(), widget.getText().toString()));
		widget.setText("");
	}

}
