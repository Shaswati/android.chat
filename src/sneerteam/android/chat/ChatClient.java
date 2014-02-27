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
				
				// If we fall off this method without doing anything it means
				//   the message is junk and we are just ignoring it.
				
				Map<String, Object> values = msg.getJavaMap();
				
				Object type = values.get("type");
				Object sender = values.get("sender");
				Object contents = values.get("contents");
				Object timestamp = values.get("timestamp");
				
				// Validate the only message we get from the server so far.
				// When we get to receiving multiple types of messages this
				//   will be broken up.
				//
				if ((type != null) && (type.toString().equals(":msg")) 
						&& (sender != null) && (contents != null) && (timestamp != null)
						&& (timestamp instanceof Long)) 
				{
					listener.on(new Message((Long)timestamp, sender.toString(), contents.toString()));
				} else {
					// debug (pode remover)
					listener.on(new Message(System.currentTimeMillis(), "<ERROR>", "<Mensagem descartada>: " + msg.getEDNString()));
				}
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
