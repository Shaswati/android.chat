package sneerteam.android.chat.ui;

import java.util.*;

import rx.*;
import rx.Observable;
import rx.android.schedulers.*;
import rx.functions.*;
import rx.observables.*;
import rx.subjects.*;
import rx.subscriptions.*;
import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import sneerteam.snapi.*;
import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.SharedPreferences.Editor;
import android.os.*;
import android.preference.*;
import android.util.*;
import android.view.*;
import android.widget.*;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class PublicChatActivity extends Activity {
	
	private static final int PICK_CONTACT_REQUEST = 100;
	
	private static List<Message> MESSAGES = initMessages();
	
	private String myNick = null;
	
	private ChatAdapter chatAdapter;
	
	private ReplaySubject<Map<String, Object>> sender = ReplaySubject.create();

	private CompositeSubscription allSubscriptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		toast("onCreate: " + System.identityHashCode(this));
		
		myNick = preferences().getString("myNick", null);
		
		setContentView(R.layout.activity_chat);
		setTitle("Public Chat");
		
		ListView listView = (ListView) findViewById(R.id.listView);
		chatAdapter = new ChatAdapter(this, R.layout.list_item_user_message, R.layout.list_item_contact_message, MESSAGES);
		chatAdapter.setSender(myNick);
		listView.setAdapter(chatAdapter);
		
		Observable<CloudConnection> cloud = CloudServiceConnection.cloudFor(this).publish().refCount();
		//Cloud cloud = Cloud.cloudFor(this);
		
		Subscription s1 = cloud
			.flatMap(new Func1<CloudConnection, Observable<PathEvent>>() {@Override public Observable<PathEvent> call(CloudConnection cloud) {
				toast("Connected");
				return cloud.path().children();
			}})
		//cloud.path().children()

			
		//Hello newcomers, this is how you publish something in the cloud:	

		//Cloud cloud = Cloud.cloudFor(this);
		//cloud.path("a", "b").pub("World");
		//cloud.path("a", "b").children();
		//cloud.path("a", "b").lastChildren();
		//cloud.path("a", "b").value();		//Sub
		//cloud.path("a", "b").lastValue(); //Get?
		//cloud.dispose(); ?

		//Subscription s = CloudConnection.cloudFor(this).repeat().subscribe(
		//	new Action1<CloudConnetion>() {
		//		void call(CloudConnection cloud) {
		//			cloud.path("a", "b").pub("World");
		//		}
		//	}
		//);
		//s.unsubscribe; ?
			
			
			
			
			
		//...
		//cloud.path("a", "b").value().subscribe(new Action<String>(){void call(String value){
		//	System.out.println("Hello " + value);
		//}});
		//
		//onDestroy:
		//cloud.dispose();

			
		//Hello newcomers, this is how you publish something in the cloud:	
		//Subscription s = Cloud.cloudFor(this).subscribe(new Action<Cloud>(){void call(Cloud cloud){
		//	cloud.path("a", "b").pub("World");
		//	...
		//	cloud.path("a", "b").value().subscribe(new Action<String>(){void call(String value){
		//		System.out.println("Hello " + value);
		//	}});
		//}});
		//
		//onDestroy:
		//s.unsubscribe();
	
			
			
			  .flatMap(new Func1<PathEvent, Observable<PathEvent>>() {@Override public Observable<PathEvent> call(PathEvent publicKey) {
			  	return publicKey.path().append("public").append("chat").children();
			  }})
			  .flatMap(new Func1<PathEvent, Observable<Message>>() {@Override public Observable<Message> call(PathEvent message) {
					return message.path().value().first().cast(Map.class).map(new Func1<Map, Message>() {@Override public Message call(Map value) {
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
					onMessage(message);
				}});

		
		Subscription s2 = Observable.combineLatest(sender, cloud.map(new Func1<CloudConnection, Path>() {
			@Override
			public Path call(CloudConnection cloud) {
				return cloud.path("public", "chat");
			}
		}), new Func2<Map<String, Object>, Path, Pair<Map<String, Object>, Path>>() {
			@Override
			public Pair<Map<String, Object>, Path> call(Map<String, Object> msg, Path path) {
				return Pair.create(msg, path.append(msg.get("timestamp")));
			}
		}).subscribe(new Action1<Pair<Map<String, Object>, Path>>() {
			@Override
			public void call(Pair<Map<String, Object>, Path> pair) {
				pair.second.pub(pair.first);
			}
		});

		allSubscriptions = Subscriptions.from(s1, s2);
		// <Nothing>
		
		//onDestroy: cloud.dispose();

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

	@Override
	protected void onDestroy() {
		toast("onDestroy: " + System.identityHashCode(this));
		allSubscriptions.unsubscribe();
		super.onDestroy();
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
		TextView widget = (TextView)findViewById(R.id.editText);
		String message = widget.getText().toString();
		try {
			long timestamp = System.currentTimeMillis();
			sender.onNext(messageBundleFor(message, timestamp));
			//cloud.path("public", "chat", timestamp).pub(message);
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
}
