package sneer.android.chat;

import sneer.chat.*;
import sneer.chat.simulator.*;
import android.app.*;

public class ChatApp extends Application {
	
	private OldChat model;

	public OldChat model() {
		if (model == null)
			model = new ChatSimulator();
//			model = new ChatImpl(this);
		return model;
	}
	
}
