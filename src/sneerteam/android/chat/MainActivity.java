package sneerteam.android.chat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new OnItemClickListener() { @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("contact_seal", contacts().get(position).getNome());
            startActivity (intent);
		}});
        
        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(this, android.R.layout.simple_list_item_1, contacts());
        listView.setAdapter(adapter);
    }
    
    private List<Contact> contacts() {
    	List<Contact> contacts = new ArrayList<Contact>();
    	contacts.add(new Contact("Altz"));
        contacts.add(new Contact("Rafa"));
        contacts.add(new Contact("Jao"));
        return contacts;
    }
    
}