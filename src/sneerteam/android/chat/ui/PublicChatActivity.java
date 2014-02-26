package sneerteam.android.chat.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sneerteam.android.chat.ChatClient;
import sneerteam.android.chat.ChatListener;
import sneerteam.android.chat.Message;
import sneerteam.android.chat.Networker;
import sneerteam.android.chat.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PublicChatActivity extends Activity {
	
	String contactSeal;
	private ChatClient chat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		contactSeal = getIntent().getExtras().getString("contact_seal");
		setTitle("Conversa com " + contactSeal);
		
		ListView listView = (ListView) findViewById(R.id.listView);
		final ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, messages());
        listView.setAdapter(adapter);
        
        try {
			chat = new ChatClient(new ChatListener() { @Override public void on(Message msg) {
				adapter.add(msg);
			}});
			chat.send("Whats happening?");
		} catch (IOException e) {
			adapter.add(new Message(e.getMessage()));
		}
	}
	
	@Override
	protected void onDestroy() {
		if (chat != null) chat.destroy();
		super.onDestroy();
	}
	
	private List<Message> messages() {
    	List<Message> messages = new ArrayList<Message>();
    	messages.add(new Message("Welcome to Public Chat"));
        return messages;
    }

}
