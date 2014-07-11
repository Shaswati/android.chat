package sneer.android.chat;

import android.app.*;
import android.content.*;
import android.os.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("sneer.android.main", "sneer.android.main.ui.ManagedContactsActivity"));
		intent.putExtra("Action", "sneer.intent.action.CHAT_CONTACT");
		intent.putExtra("title", "Sneer Chat");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		
	}
}
