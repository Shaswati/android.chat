package sneerteam.android.chat.ui;

import java.util.ArrayList;
import java.util.List;

import sneerteam.android.chat.Message;
import sneerteam.android.chat.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChatActivity extends Activity {
	
	String contactSeal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		contactSeal = getIntent().getExtras().getString("contact_seal");
		setTitle("Conversa com " + contactSeal);
		
		ListView listView = (ListView) findViewById(R.id.listView);
		ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, messages());
        listView.setAdapter(adapter);
	}
	
	private List<Message> messages() {
    	List<Message> messages = new ArrayList<Message>();
    	messages.add(new Message("Ola!"));
        messages.add(new Message("Tudo bom?"));
        messages.add(new Message("Tudo, e voce?"));
        return messages;
    }

}
