package sneerteam.android.chat;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListView listView = (ListView) findViewById(R.id.listView);
        
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