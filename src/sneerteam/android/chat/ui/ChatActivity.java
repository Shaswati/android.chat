package sneerteam.android.chat.ui;

import java.util.ArrayList;
import java.util.List;

import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ChatActivity extends Activity {
	
	String contactPublicKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		contactPublicKey = getIntent().getExtras().getString("contact_public_key");
		setTitle("Conversa com " + contactPublicKey);
		
		ListView listView = (ListView) findViewById(R.id.listView);
		ChatAdapter adapter = new ChatAdapter(this, R.layout.list_item_user_message, R.layout.list_item_contact_message, messages());
        listView.setAdapter(adapter);
	}
	
	private List<Message> messages() {
    	List<Message> messages = new ArrayList<Message>();
    	messages.add(new Message(0, "Eu", "Ola!"));
        messages.add(new Message(1, "ze", "Tudo bom?"));
        messages.add(new Message(2, "jose", "Tudo, e voce?"));
        return messages;
    }

}
