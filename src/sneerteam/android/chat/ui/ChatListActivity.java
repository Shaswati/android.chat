package sneerteam.android.chat.ui;

import sneerteam.android.chat.Contact;
import sneerteam.android.chat.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
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
				String name = extras.get("name").toString();
				String nickname = extras.get("nickname").toString();
				chatListFragment.addContact(new Contact(publicKey, name, nickname));
				toast(publicKey + "\n" + nickname);
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_list);
		
		chatListFragment = (ChatListFragment) getSupportFragmentManager().findFragmentById(
				R.id.chat_list);

		if (findViewById(R.id.chat_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			chatListFragment.setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	/**
	 * Callback method from {@link ChatListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Contact contact) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(ChatDetailFragment.ARG_ITEM_PUBLIC_KEY, contact.getPublicKey());
			arguments.putString(ChatDetailFragment.ARG_ITEM_NICKNAME, contact.getNickname());
			ChatDetailFragment fragment = new ChatDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.chat_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ChatDetailActivity.class);
			detailIntent.putExtra(ChatDetailFragment.ARG_ITEM_PUBLIC_KEY, contact.getPublicKey());
			detailIntent.putExtra(ChatDetailFragment.ARG_ITEM_NICKNAME, contact.getNickname());
			startActivity(detailIntent);
		}
	}
	
	void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
