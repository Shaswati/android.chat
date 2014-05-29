package sneerteam.android.chat.ui;

import rx.android.schedulers.*;
import rx.functions.*;
import sneerteam.android.chat.*;
import sneerteam.android.chat.R;
import sneerteam.snapi.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;

/**
 * An activity representing a list of Chats. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ChatDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ChatListFragment} and the item details (if present) is a
 * {@link ChatDetailFragment}.
 * <p>
 * This activity also implements the required {@link ChatListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class ChatListActivity extends FragmentActivity implements
		ChatListFragment.Callbacks {
	
	private static ChatListFragment chatListFragment;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	private int currentContactRequestId;

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_contacts)
			ContactPicker.startActivityForResult(this, ++currentContactRequestId);
		return true;
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode != RESULT_OK) return;
		if (requestCode != currentContactRequestId) return;
		
		String publicKey = ContactPicker.publicKeyFrom(intent);
		chatApp().room(publicKey).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Room>() {@Override public void call(Room room) {
			onItemSelected(room);
		}});
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_list);
		
		ChatApp app = chatApp();
		app.rooms().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Room>() {@Override public void call(Room room) {
			chatListFragment.addRom(room);
		}});
		
		chatListFragment = (ChatListFragment) getSupportFragmentManager().findFragmentById(
				R.id.chat_list);

		if (findViewById(R.id.chat_detail_container) != null) {
			mTwoPane = true;
			chatListFragment.setActivateOnItemClick(true);
		}
	}
	
	
	protected void log(String string) {
		Log.d(ChatListActivity.class.getSimpleName(), string);
	}

	
	/**
	 * Callback method from {@link ChatListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Room room) {
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString("room", room.id());
			ChatDetailFragment fragment = new ChatDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.chat_detail_container, fragment).commit();

		} else {
			Intent detailIntent = new Intent(this, ChatDetailActivity.class);
			detailIntent.putExtra("room", room.id());
			startActivity(detailIntent);
		}
	}
	
	
	private ChatApp chatApp() {
		return (ChatApp)getApplication();
	}

	
	void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
