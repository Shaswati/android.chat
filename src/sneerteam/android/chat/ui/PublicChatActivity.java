package sneerteam.android.chat.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import sneerteam.snapi.CloudConnection;
import sneerteam.snapi.Path;
import sneerteam.snapi.PathEvent;
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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PublicChatActivity extends Activity {
	
	CloudConnection cloud;
	Path chatPath;
	Subscription subscription;
	
	final ServiceConnection snapi = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			toast("connected");
			cloud = new CloudConnection(binder);
			chatPath = cloud.path("public", "chat");
			
			subscription = cloud.path()
			  .children()
			  .flatMap(new Func1<PathEvent, Observable<PathEvent>>() {@Override public Observable<PathEvent> call(PathEvent publicKey) {
			  	return publicKey.path().append("public").append("chat").children();
			  }})
			  .flatMap(new Func1<PathEvent, Observable<Message>>() {@Override public Observable<Message> call(PathEvent child) {
					return child.path().value().first().cast(Map.class).map(new Func1<Map, Message>() {@Override public Message call(Map value) {
						long timestamp = (Long) value.get("timestamp");
						String sender = (String) value.get("sender");
						String contents = (String) value.get("contents");
						return new Message(timestamp, sender, contents);
					}});
				}})
				.onErrorResumeNext(new Func1<Throwable, Observable<Message>>() {@Override public Observable<Message> call(Throwable error) {
					return Observable.from(new Message(System.currentTimeMillis(), "<system>", error.toString()));
			  }})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<Message>() {@Override public void call(Message message) {
					toast(message.content());
					onMessage(message);
				}});
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			toast("disconnected");
			reset();
		}
	};
	
	private static final int PICK_CONTACT_REQUEST = 100;
	
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
		bindIntent.setClassName("sneerteam.android.main", "sneerteam.android.main.CloudService");
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
			reset();
			unbindService(snapi);
		}
	}

	private void unsubscribe() {
		if (subscription != null) {
			subscription.unsubscribe();
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
		if (chatPath == null)
			return;
		
		TextView widget = (TextView)findViewById(R.id.editText);
		String message = widget.getText().toString();
		try {
			long timestamp = System.currentTimeMillis();
			chatPath.append(timestamp).pub(messageBundleFor(message, timestamp));
		} catch (Exception e) {
			toast(e.getMessage());
			e.printStackTrace();
			return;
		}
		widget.setText("");
	}
	
	Map<String, Object> messageBundleFor(String text, long timestamp) {
		Map<String, Object> bundle = new HashMap<String, Object>();
		bundle.put("intent", "message");
		bundle.put("sender", myNick);
		bundle.put("contents", text);
		bundle.put("timestamp", timestamp);
		return bundle;
	}
	
	private SharedPreferences preferences() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	void toast(String message) {
		Toast.makeText(PublicChatActivity.this, message, Toast.LENGTH_LONG).show();
	}

	private void reset() {
		chatPath = null;
		subscription = null;
		cloud = null;
	}
}
