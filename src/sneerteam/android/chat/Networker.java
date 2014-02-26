package sneerteam.android.chat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/*
 * Mono-threaded, high-latency, low-cpu-overhead UDP-based messaging API for implementing
 *   the "EDN chat protocol" for the Sneer chat app.
 *   
 * NOTE: The API (methods here) will change a lot still.
 * 
 * Call this from the UI thread. Use handlers:
 *   http://www.mopri.de/2010/timertask-bad-do-it-the-android-way-use-a-handler/
 * 
 * The run() method is the polling method, already named "run" so you can treat the
 *   Networker as a recurrent runnable for Android handlers.
 * 
 * The networker translates your java objects into the server's EDN-string protocol
 *   and back (uses CMSMessage internally) and also hides what it is doing wth sockets
 *   to send your stuff (currently, just dispatches each message into a single outgoing
 *   UDP packet).  
 */
public class Networker implements Runnable {
	
	// Cache of resolved IP address for "dynamic.sneer.me"
	SocketAddress serveraddr; 

	// who is going to get our callbacks 
	NetworkerListener listener;
	
	// our UDP socket
	DatagramChannel channel;
	
	public Networker(NetworkerListener listener) throws IOException, UnknownHostException {
		this.listener = listener;

		// dynamic.sneer.me:55555
		serveraddr = new InetSocketAddress(InetAddress.getByName("dynamic.sneer.me"), 55555);
		
		// open the local nonblocking UDP socket
		channel = DatagramChannel.open();
		channel.configureBlocking(false);
		channel.socket().bind(null); // is this needed? test taking it out
	}
	
	// send a chat message
	public void sendChat(String sender, String message) {
		
		// FIXME: maybe we should use our spiffy CMSMessage class, hmm? :) 
		// FIXME: escape double quotes in the 'message' string.
		String edn = "{:type :msg :contents \"" + message + "\"}";
		
		byte[] sendData;
		try {
			sendData = edn.getBytes("UTF-8");
			channel.send(ByteBuffer.wrap(sendData), serveraddr);
			listener.sentPacket();
		} catch (UnsupportedEncodingException e) {
			listener.error();
		} catch (IOException e) {
			listener.error();
		}
	}
	
	// invoke this periodically using the UI thread (no multithreading support)
	public void run() {
		
		// we just polled the network
		listener.polled();
		
		// pump out all UDP packets from the socket ("channel" which is the nonblocking wrapper)
		while (true) {
			ByteBuffer data = ByteBuffer.allocate(4096);
			try {
				SocketAddress sender = null;
				sender = channel.receive(data);
				if (sender == null) {
					break; // we're done -- no packets
				} else {
					// else we got one
					data.flip();
					listener.receivedPacket(data);
				
					// 	FIXME: parse the packet, the EDN and call app with a sensible
					//   bag of Java objects and a meaning (incoming chat msg, etc.)
				}
				
			} catch (IOException e) {
				listener.error();
				break;
			}
		}
	}
	
	
}
