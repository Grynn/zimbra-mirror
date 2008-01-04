/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;

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
			} catch (Exception x) {
				try {
					OfflineSyncManager.getInstance().processSyncException(ombx.getAccount(), x);
				} catch (ServiceException se) {
					OfflineLog.offline.error("unexpected exception", se);
				}
				OfflinePoller.this.done(ombx, false);
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
			ombx.syncNow();
		}
	}
}
