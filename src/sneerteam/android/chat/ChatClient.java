package sneerteam.android.chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;

public class ChatClient {

	private ChatListener listener;
	private LoopingHandlerRunnable loopingNetworker;
	private Networker networker;

	public ChatClient(ChatListener chatListener) throws IOException {
		listener = chatListener;
		networker = createNetworker();
		loopingNetworker = new LoopingHandlerRunnable(networker, 100);
		
		networker.send("{:type :whats-up}");
	}

	private Networker createNetworker() throws IOException {
		
		return new Networker(new NetworkerListener() {
			
			@Override
			public void sentPacket() {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void receivedPacket(ByteBuffer packet) {
				byte[] bytes = new byte[packet.remaining()];
				packet.get(bytes);
				CMSMessage msg;
				try {
					msg = new CMSMessage(new String(bytes, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new IllegalStateException();
				}
				Map<String, Object> values = msg.getJavaMap();
				Object type = values.get("type");
				//if (!":msg".equals(type)) return;
						if (values.get("contents") == null) return;
				
				listener.on(new Message("" + values.get("sender") + ": " + values.get("contents")));
				
			}
			
			@Override
			public void polled() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void error() {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public void destroy() {
		loopingNetworker.stop();
	}

	public void send(String message) {
		networker.sendChat("Dummy", message);
	}

	
}
