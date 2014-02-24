package sneerteam.android.chat;

/*
 * Callbacks from the Networker class.
 * 
 * NOTE: these will change a lot still.
 * 
 */
public interface NetworkerListener {
	
	// invoked whenever the networker polls
	public void polled();

	// received a packet
	public void receivedPacket();
	
	// sent a packet
	public void sentPacket();
	
	// some socket IO error
	public void error();
}
