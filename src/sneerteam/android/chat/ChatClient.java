package sneerteam.android.chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

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
				listener.on(new Message("Message..."));
				byte[] bytes = new byte[packet.remaining()];
				packet.get(bytes);
				Message msg;
				try {
					msg = new Message(new String(bytes, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new IllegalStateException();
				}
				listener.on(msg);
				
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
		networker.sendChat("Annonymous", message);
	}

	
}
