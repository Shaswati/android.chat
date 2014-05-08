package sneerteam.android.chat.ui;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import sneerteam.android.chat.Contact;
import sneerteam.android.chat.R;
import sneerteam.snapi.Cloud;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
	
	private static final int PICK_CONTACT_REQUEST = 100;
	
	private static ChatListFragment chatListFragment;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	private Cloud cloud;

	private Contact contact;
	

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
				String nickname = extras.get("nickname").toString();
				final Contact contact = new Contact(publicKey, nickname);
				chatListFragment.addContact(contact);
				onItemSelected(contact);
				cloud.path(publicKey, "info", "name").value().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Object>() {@Override public void call(Object name) {
					contact.setName((String) name);
					chatListFragment.contactChanged(contact);
				}});
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_list);
		
		cloud = Cloud.cloudFor(this);
		
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
	
	@Override
	protected void onDestroy() {
		cloud.dispose();
		super.onDestroy();
	}

	/**
	 * Callback method from {@link ChatListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Contact contact) {
		this.contact = contact;
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putSerializable("contact", contact);
			ChatDetailFragment fragment = new ChatDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.chat_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ChatDetailActivity.class);
			detailIntent.putExtra("contact", contact);
			startActivity(detailIntent);
		}
	}
	
	public void onSendButtonClick(View view) {
		TextView widget = (TextView)findViewById(R.id.editText);
		String message = widget.getText().toString();
		cloud.path("contacts", contact.getPublicKey(), "chat", System.currentTimeMillis()).pub(message);
		widget.setText("");
	}
	
	void toast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
