package sneer.android.chat.ui;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import sneer.android.chat.ChatApp;
import sneer.android.chat.R;
import sneer.chat.OldChat;
import sneer.chat.OldRoom;
import sneer.snapi.Contact;
import sneer.snapi.SneerUtils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * This activity has different
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
 */
public class ChatListActivity extends FragmentActivity implements ChatListFragment.Callbacks {

	private static ChatListFragment chatListFragment;

	/** Whether or not the activity is in two-pane mode, i.e. running on a tablet device. */
	private boolean mTwoPane;

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_contacts)
			chat().pickContact().subscribe(new Action1<Contact>() {
				@Override
				public void call(Contact contact) {
					onItemSelected(chat().produceRoomWith(contact));
				}
			});

		return true;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_list);

		SneerUtils.showSneerInstallationMessageIfNecessary(this);

		chat().rooms().observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<OldRoom>() {
					@Override
					public void call(OldRoom room) {
						chatListFragment.addRom(room);
					}
				});

		chatListFragment = (ChatListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.chat_list);

		if (findViewById(R.id.chat_detail_container) != null) {
			mTwoPane = true;
			chatListFragment.setActivateOnItemClick(true);
		}
	}

	
	protected void log(String string) {
		Log.d(ChatListActivity.class.getSimpleName(), string);
	}

	
	/** Callback method from {@link ChatListFragment.Callbacks} indicating that the item with the given ID was selected. */
	@Override
	public void onItemSelected(OldRoom room) {
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putString(ChatDetailFragment.CONTACT_PUK,
					room.contactPublicKey());
			ChatDetailFragment fragment = new ChatDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.chat_detail_container, fragment).commit();

		} else {
			Intent detailIntent = new Intent(this, ChatDetailActivity.class);
			detailIntent.putExtra(ChatDetailFragment.CONTACT_PUK,
					room.contactPublicKey());
			startActivity(detailIntent);
		}
	}

	
	private OldChat chat() {
		return ((ChatApp) getApplication()).model();
	}

	
	void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
