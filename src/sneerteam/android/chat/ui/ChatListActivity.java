package sneerteam.android.chat.ui;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import sneerteam.android.chat.*;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.Toast;

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
	
	private static final int PICK_CONTACT_REQUEST = 100;
	
	private static ChatListFragment chatListFragment;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_contacts:
				pickContact();
				break;
		}
		return true;
	}
	
	private void pickContact() {
		Intent intent = new Intent("sneerteam.intent.action.PICK_CONTACT");
		startActivityForResult(intent, PICK_CONTACT_REQUEST);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == PICK_CONTACT_REQUEST) {
			if (resultCode == RESULT_OK) {
				Bundle extras = intent.getExtras();
				String publicKey = extras.get("public_key").toString();
				
				ChatApp app = (ChatApp)getApplication();
				app.room(publicKey).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Room>() {@Override public void call(Room room) {
					onItemSelected(room);
				}});

			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_list);
		
		ChatApp app = (ChatApp)getApplication();
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
	
	void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
