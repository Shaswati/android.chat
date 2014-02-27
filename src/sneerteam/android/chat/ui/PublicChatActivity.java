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
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PublicChatActivity extends Activity {
	
	String contactSeal;
	private ChatClient chat;
	
	List<Message> myMessages = messages();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		contactSeal = getIntent().getExtras().getString("contact_seal");
		setTitle("Conversa com " + contactSeal);
		
		ListView listView = (ListView) findViewById(R.id.listView);
		final ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, myMessages);
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
		String whatusay = ((TextView)findViewById(R.id.editText)).getText().toString();
		chat.send(whatusay);
	}

}
