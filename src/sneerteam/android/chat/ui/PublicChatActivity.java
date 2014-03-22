package sneerteam.android.chat.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import sneerteam.api.ICloud;
import sneerteam.api.ISubscriber;
import sneerteam.api.ISubscription;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PublicChatActivity extends Activity {
	
	volatile ICloud cloud;
	volatile ISubscription subscription;
	
	final ServiceConnection snapi = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			toast("connected");
			cloud = ICloud.Stub.asInterface(binder);
			try {
				subscription = cloud.sub(chatUri(), subscriber);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			toast("disconnected");
			subscription = null;
			cloud = null;
		}
	};
	
	final ISubscriber subscriber = new ISubscriber.Stub() {
		@Override
		public void on(Uri path, Bundle bundle) throws RemoteException {
			
			Bundle value = bundle.getBundle(":value");
			
			long timestamp = value.getLong(":timestamp");
			String sender = value.getString(":sender");
			String contents = value.getString(":contents");
			Message chatMessage = new Message(timestamp, sender, contents);
			
			// the callback will be dispatched in a thread pool thread
			// so to update the UI we need a handler to get it there 
			handler.sendMessage(android.os.Message.obtain(handler, ON_CHAT_MESSAGE, chatMessage));
		}
	};
	
	private static final int ON_CHAT_MESSAGE = 1;
	private static final int PICK_CONTACT_REQUEST = 100;
	
	final Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message m) {
			switch (m.what) {
			case ON_CHAT_MESSAGE:
				onMessage((Message)m.obj);
				break;
			default:
				super.handleMessage(m);
			}
		}
	};
	
	private static List<Message> MESSAGES = initMessages();
	
	private String myNick = null;
	
	private ChatAdapter chatAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myNick = preferences().getString("myNick", null);
		
		setContentView(R.layout.activity_chat);
		setTitle("Public Chat");
		
		ListView listView = (ListView) findViewById(R.id.listView);
		chatAdapter = new ChatAdapter(this, R.layout.list_item_user_message, R.layout.list_item_contact_message, MESSAGES);
		chatAdapter.setSender(myNick);
		listView.setAdapter(chatAdapter);
		
		connect();
	}
	
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
				Object publicKey = extras.get("public_key");
				Object nickname = extras.get("nickname");
				toast(publicKey + "\n" + nickname);
			}
    }
	}
	
	private void connect() {
		bindService(
				bindCloudServiceIntent(),
				snapi,
				Context.BIND_AUTO_CREATE + Context.BIND_ADJUST_WITH_ACTIVITY);
	}

	private Intent bindCloudServiceIntent() {
		Intent bindIntent = new Intent("sneerteam.intent.action.BIND_CLOUD_SERVICE");
		bindIntent.setClassName("sneerteam.snapi", "sneerteam.snapi.CloudService");
		return bindIntent;
	}

	@Override
	protected void onDestroy() {
		unsubscribe();
		disconnect();
		super.onDestroy();
	}

	private void disconnect() {
		if (cloud != null) {
			cloud = null;
			unbindService(snapi);
		}
	}

	private void unsubscribe() {
		if (subscription != null) {
			try {
				subscription.dispose();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			subscription = null;
		}
	}
	
	private static List<Message> initMessages() {
		List<Message> messages = new ArrayList<Message>();
		messages.add(new Message(0, "Sneer", "Welcome to the public chat room. Be awesome."));
		return messages;
	}
	
	void onMessage(Message msg) {
		int insertionPointHint = Collections.binarySearch(MESSAGES, msg);
		if (insertionPointHint < 0) {
			int insertionPoint = Math.abs(insertionPointHint) - 1;
			MESSAGES.add(insertionPoint, msg);
			chatAdapter.notifyDataSetChanged();
		}
	}
	
	public void onSendButtonClick(View view) {
		if (!chatAdapter.hasSender())
			tryToGetMyNick();
		else
			sendText();
	}

	private void tryToGetMyNick() {
		final EditText input = new EditText(this);
		input.setSingleLine();
		
		new AlertDialog.Builder(this)
		    .setTitle("Name")
		    .setMessage("Enter your full name or a nice nick that people will know.")
		    .setView(input)
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int whichButton) {
	        	myNick = input.getText().toString();
	        	Editor editor = preferences().edit();
	        	editor.putString("myNick", myNick);
	        	editor.commit();
	        	chatAdapter.setSender(myNick);
	        	sendText();
		    }}).show();
	}
	
	private void sendText() {
		if (cloud == null)
			return;
		
		TextView widget = (TextView)findViewById(R.id.editText);
		String message = widget.getText().toString();
		try {
			cloud.pub(chatUri(), bundle(message));
		} catch (RemoteException e) {
			toast(e.getMessage());
			e.printStackTrace();
			return;
		}
		widget.setText("");
	}

	private Bundle bundle(String message) {
		Bundle bundle = new Bundle();
		bundle.putBundle(":value", messageBundleFor(message));
		return bundle;
	}

	private Bundle messageBundleFor(String text) {
		Bundle bundle = new Bundle();
		bundle.putString(":type", ":msg");
		bundle.putString(":sender", myNick);
		bundle.putString(":contents", text);
		bundle.putLong(":timestamp", System.currentTimeMillis());
		return bundle;
	}
	
	private SharedPreferences preferences() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	void toast(String message) {
		Toast.makeText(PublicChatActivity.this, message, Toast.LENGTH_LONG).show();
	}
	
	Uri chatUri() {
		return Uri.parse("/public/chat");
	}
}
