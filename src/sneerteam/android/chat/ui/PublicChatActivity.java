package sneerteam.android.chat.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sneerteam.android.chat.ChatClient;
import sneerteam.android.chat.ChatListener;
import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PublicChatActivity extends Activity {
	
	private ChatClient chat;
	private String myNick = null;
	
	List<Message> myMessages = messages();

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
        
		setContentView(R.layout.activity_chat);
		
		setTitle("Public Chat");
		
		ListView listView = (ListView) findViewById(R.id.listView);
        final ChatAdapter adapter = new ChatAdapter(this, R.layout.list_item_user_message, R.layout.list_item_contact_message, myMessages);
        listView.setAdapter(adapter);
        
        try {
			chat = new ChatClient(new ChatListener() { @Override public void on(Message msg) {
				if (Collections.binarySearch(myMessages, msg) < 0) {
					myMessages.add(msg);
					Collections.sort(myMessages);
					adapter.notifyDataSetChanged();
				}
			}});
		} catch (IOException e) {
			adapter.add(new Message(System.currentTimeMillis(), "<ERROR>", e.getMessage()));
		}
	}
	
	@Override
	protected void onDestroy() {
		if (chat != null) chat.destroy();
		super.onDestroy();
	}
	
	private List<Message> messages() {
    	List<Message> messages = new ArrayList<Message>();
    	messages.add(new Message(System.currentTimeMillis(), "Sneer", "Welcome to Public Chat"));
        return messages;
    }
	
	public void onSendButtonClick(View view) {
		if (myNick == null)
			tryToGetMyNick();
		else
			sendText();
	}

	private void tryToGetMyNick() {
		final EditText input = new EditText(this);
		input.setSingleLine();
		
		new AlertDialog.Builder(this)
		    .setTitle("Update Status")
		    .setMessage("Nick:")
		    .setView(input)
		    .setPositiveButton("Ok", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int whichButton) {
	        	myNick = input.getText().toString();
	        	sendText();
		    }}).show();
	}
	
	private void sendText() {
		String whatusay = ((TextView)findViewById(R.id.editText)).getText().toString();
		chat.send(myNick, whatusay);
	}

}
