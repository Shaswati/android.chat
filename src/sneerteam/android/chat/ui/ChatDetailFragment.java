package sneerteam.android.chat.ui;

import java.util.ArrayList;
import java.util.List;

import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import sneerteam.android.chat.dummy.DummyContent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
	private static List<Message> MESSAGES = initMessages();
	private ChatAdapter chatAdapter;
	public static final String ARG_ITEM_PUBLIC_KEY = "public_key";
	public static final String ARG_ITEM_NICKNAME = "nickname";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ChatDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String nickname = getArguments().getString(ARG_ITEM_NICKNAME);
		this.getActivity().setTitle(nickname);
		
		// TODO: move to activity!
		ListView listView = (ListView) this.getActivity().findViewById(R.id.listView);
		chatAdapter = new ChatAdapter(this.getActivity(), R.layout.list_item_user_message, R.layout.list_item_contact_message, MESSAGES);
		chatAdapter.setSender(nickname);
		listView.setAdapter(chatAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_chat_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.chat_detail))
					.setText(mItem.content);
		}

		return rootView;
	}
	
	private static List<Message> initMessages() {
		List<Message> messages = new ArrayList<Message>();
		messages.add(new Message(0, "Sneer", "Welcome to chat room. Be awesome."));
		return messages;
	}
}
