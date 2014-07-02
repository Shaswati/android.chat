package sneerteam.android.chat;

import sneerteam.android.chat.impl.*;
import android.app.*;

public class ChatApp extends Application {
	
	private Chat model;

	public Chat model() {
		if (model == null)
			model = new ChatImpl(this);
//			model = new ChatSimulator();
		return model;
	}
	
}
