package sneerteam.android.chat;

import android.os.Handler;

/*
 * A Runnable given to an Android Handler that sets itself up for being called again
 *   until someone tells it to stop doing that. 
 *   
 * Intervals are in milliseconds.
 * 
 * On the constructor this actually posts the runnable to run after the specified interval. 
 */
public class LoopingHandlerRunnable implements Runnable {

	Handler handler;
	Runnable inner;
	int interval;
	boolean stopped = false;
	
	// remove a crap
	public LoopingHandlerRunnable(Runnable inner, int interval) {
		this.handler = new Handler();
		this.inner = inner;
		this.interval = interval;
		handler.postDelayed(this, interval);
	}
	
	public void stop() {
		stopped = true;
	}
	
	public boolean isStopped() {
		return stopped;
	}

	@Override
	public void run() {
		if (! stopped) {
			inner.run();
			handler.postDelayed(this, interval);
		}
	}
}
