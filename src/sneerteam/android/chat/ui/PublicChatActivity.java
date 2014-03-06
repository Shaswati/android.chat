package sneerteam.android.chat.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneerteam.android.chat.ChatClient;
import sneerteam.android.chat.ChatListener;
import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PublicChatActivity extends Activity {
	
	private static List<Message> MESSAGES = initMessages();

	private ChatClient chat;
	private String myNick = null;
	
	private ChatAdapter chatAdapter;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ------------------------------------------------------------
        // Remove the retarded "must run networking on ANOTHER thread"
        //   limitation imposed by Android.
        // We know what we're doing and adding another thread will NOT 
        //   help us in any way, shape or form.
        // ------------------------------------------------------------

        if (android.os.Build.VERSION.SDK_INT > 9) {
    		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    		StrictMode.setThreadPolicy(policy);
    	}
        
        myNick = preferences().getString("myNick", null);
        
		setContentView(R.layout.activity_chat);
		
		setTitle("Public Chat");
		
		ListView listView = (ListView) findViewById(R.id.listView);
		chatAdapter = new ChatAdapter(this, R.layout.list_item_user_message, R.layout.list_item_contact_message, MESSAGES);
		chatAdapter.setSender(myNick);
        listView.setAdapter(chatAdapter);
        
        try {
			chat = new ChatClient(new ChatListener() { @Override public void on(Message msg) {
				if (Collections.binarySearch(MESSAGES, msg) < 0) {
					MESSAGES.add(msg);
					Collections.sort(MESSAGES);
					chatAdapter.notifyDataSetChanged();
				}
			}});
		} catch (IOException e) {
			chatAdapter.add(new Message(System.currentTimeMillis(), "<ERROR>", e.getMessage()));
		}
	}

	@Override
	protected void onDestroy() {
		if (chat != null) chat.destroy();
		super.onDestroy();
	}
	
	private static List<Message> initMessages() {
    	List<Message> messages = new ArrayList<Message>();
    	messages.add(new Message(0, "Sneer", "Welcome to the public chat room. Be awesome."));
        return messages;
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
		String whatusay = widget.getText().toString();
		widget.setText("");
		chat.send(myNick, whatusay);
	}
	
	private SharedPreferences preferences() {
		return PreferenceManager.getDefaultSharedPreferences(this);
	}

}
