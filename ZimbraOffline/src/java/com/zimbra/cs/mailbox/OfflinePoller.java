package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.cs.mailbox.OfflineMailbox.SyncState;
import com.zimbra.cs.offline.OfflineLog;

public class OfflinePoller {
	private static OfflinePoller instance = new OfflinePoller();
	private OfflinePoller() {}
	public static OfflinePoller getInstance() {
		return instance;
	}
	
	private class Poller implements Runnable {

		private OfflineMailbox ombx;
		
		Poller(OfflineMailbox ombx) {
			this.ombx = ombx;
		}
		
		public void run() {
			try {
				ombx.pollForUpdates(); //will block until server responds or timeout
				OfflinePoller.this.done(ombx, true);
			} catch (Throwable t) {
				OfflineLog.offline.warn(t);
				OfflinePoller.this.done(ombx, false);
				ombx.setSyncState(SyncState.OFFLINE);
			}
		}
	}
	
	private List<OfflineMailbox> pollQueue = new ArrayList<OfflineMailbox>();
	private List<OfflineMailbox> doneQueue = new ArrayList<OfflineMailbox>();
	
	public synchronized boolean isSyncCandidate(OfflineMailbox ombx) {
		if (doneQueue.remove(ombx)) {
			return true;
		}
		if (pollQueue.contains(ombx)) {
			return false;
		}
		
		//TODO: may need a pool to avoid creating new thread every time
		new Thread(new Poller(ombx)).start();
		pollQueue.add(ombx);
		return false;
	}
	
	private synchronized void done(OfflineMailbox ombx, boolean success) {
		pollQueue.remove(ombx);
		if (success) {
			doneQueue.add(ombx);
		}
	}
}
