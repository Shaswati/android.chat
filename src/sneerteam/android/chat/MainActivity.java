package sneerteam.android.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements NetworkerListener {
	
	// Object that solves networking for the chat app.
	Networker networker;
	
	// Contraption that will call the Networker using the UI thread every 100ms or so.
	LoopingHandlerRunnable loopingNetworker;
	
	// debugging
	int pollcount = 0;
	int sendcount = 0;
	int receivecount = 0;
	int errorcount = 0;
	
	// helper for spammer test
	int spamskip = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    	
        // ------------------------------------------------------------
        // UI init
        // ------------------------------------------------------------

        setContentView(R.layout.activity_main);
        
        /*
           // contacts test  
        	ListView listView = (ListView) findViewById(R.id.listView);
        	ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(this, android.R.layout.simple_list_item_1, contacts());
        	listView.setAdapter(adapter);
        */
        
    	setContentView(R.layout.activity_main);
    	(( TextView )findViewById(R.id.textView1) ).setText("Starting network thread.");
        
        // ------------------------------------------------------------
        // Network init
        // ------------------------------------------------------------

        // create networker
    	try {
    		networker = new Networker(this);
    		// start networker with 100ms (good enough for chatting) poll 
    		//   on the UI thread
    		loopingNetworker = new LoopingHandlerRunnable(networker, 100);
    	} catch (IOException e) {
    		(( TextView )findViewById(R.id.textView1) ).setText("IOException: " + e);
    	}
    }

	@Override
	public void polled() {
		
		// we will abuse the poller to send a packet once every second
		spamskip++;
		if (spamskip >= 10) { // 10 x 100ms = 1s
			spamskip = 0;
			// send a spam message to the server
			networker.sendChat("Humpty Dumpty", "I just fell off of a wall. Ouch!");
		}
				
		// say we polled
		pollcount++;
		updateUI();
	}

	@Override
	public void receivedPacket() {
		receivecount++;
		updateUI();
	}

	@Override
	public void error() {
		errorcount++;
		updateUI();
	}
	
	@Override
	public void sentPacket() {
		sendcount++;
		updateUI();
	}
	
	void updateUI() {
		(( TextView )findViewById(R.id.textView1) )
			.setText("Poll " + pollcount + " Send " + sendcount + " Rcv " + receivecount + " Err " + errorcount);		
	}
	
    /*
    // Creates a contact list for the demo display
    private List<Contact> contacts() {
    	List<Contact> contacts = new ArrayList<Contact>();
    	contacts.add(new Contact("Altz"));
        contacts.add(new Contact("Rafa"));
        contacts.add(new Contact("Jao"));
        contacts.add(new Contact("Jao 2"));
        contacts.add(new Contact("Jao ayhuewuaghe"));
        return contacts;
    }
    */
    
}