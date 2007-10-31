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

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;

public class MailboxSync {
    
    private static final String SN_OFFLINE  = "offline";
    private static final String FN_PROGRESS = "state";
    private static final String FN_TOKEN    = "token";
    private static final String FN_INITIAL  = "initial";
    private static final String FN_LAST_ID  = "last";
    
    private enum SyncStage {
        BLANK, INITIAL, SYNC, RESET
    }
	
    private SyncStage mStage = SyncStage.BLANK;
    private boolean mSyncRunning = false;
    
    private String mSyncToken;
    private Element mInitialSync;
    private int mLastSyncedItem;
    
    private OfflineMailbox ombx;

    MailboxSync(OfflineMailbox ombx) throws ServiceException {
    	this.ombx = ombx;
    	
        Metadata config = ombx.getConfig(null, SN_OFFLINE);
        if (config != null && config.containsKey(FN_PROGRESS)) {
            try {
            	setStage(SyncStage.valueOf(config.get(FN_PROGRESS)));
                switch (mStage) {
                    case INITIAL:  mInitialSync = Element.parseXML(config.get(FN_INITIAL, null));
                                   mLastSyncedItem = (int) config.getLong(FN_LAST_ID, 0);          break;
                    case SYNC:     mSyncToken = config.get(FN_TOKEN, null);                        break;
                }
            } catch (Exception e) {
                ZimbraLog.mailbox.warn("invalid persisted sync data; will force reset");
                setStage(SyncStage.RESET);
            }
        }
    }
    
    private boolean lockMailboxToSync() {
    	if (!mSyncRunning) {
	    	synchronized (this) {
	    		if (!mSyncRunning) {
	    			mSyncRunning = true;
	    			return true;
	    		}
	    	}
    	}
    	return false;
    }
    
    private void unlockMailbox() {
    	assert mSyncRunning == true;
    	mSyncRunning = false;
    }
    
    void sync(boolean isOnRequest) throws ServiceException {
       	OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        if (lockMailboxToSync()) { //don't want to start another sync when one is already in progress
            try {
            	String user = ombx.getRemoteUser();
                if (mStage == SyncStage.RESET) {
                    String acctId = ombx.getAccountId();
                    ombx.deleteMailbox();
                    Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(acctId);
                    if (!(mbox instanceof OfflineMailbox)) {
                        OfflineLog.offline.debug("cannot sync: not an OfflineMailbox for account " + user);
                        return;
                    }
                    ombx = (OfflineMailbox) mbox;
                }
                
                if (mStage == SyncStage.SYNC)
                	PushChanges.sendPendingMessages(ombx, isOnRequest);
            	
            	if (!isOnRequest) {
        	    	if (!syncMan.reauthOK(ombx.getAccount()))
        	    		return;
        	    	
        	    	if (mStage == SyncStage.SYNC &&
        	    			!(syncMan.isOnLine(user) &&
        				        OfflineLC.zdesktop_enable_push.booleanValue() &&
        					    ombx.getRemoteServerVersion().getMajor() >= 5 &&
        					    OfflinePoller.getInstance().isSyncCandidate(ombx)) &&
        					System.currentTimeMillis() - syncMan.getLastTryTime(user) < ombx.getOfflineAccount().getSyncFrequency())
        	    		return;
        	    }
                
                syncMan.syncStart(user);

                if (mStage == SyncStage.BLANK)
                    InitialSync.sync(ombx);
                else if (mStage == SyncStage.INITIAL)
                    InitialSync.resume(ombx);
                
                DeltaSync.sync(ombx);
                if (PushChanges.sync(ombx, isOnRequest))
                    DeltaSync.sync(ombx);

                syncMan.syncComplete(user);
            } catch (Exception e) {
                syncMan.processSyncException(ombx.getAccount(), e);
            } finally {
            	unlockMailbox();
            }
        } else {
        	OfflineLog.offline.debug("sync already in progress");
        }
    }
    
    /** Returns the sync token from the last completed initial or delta sync,
     *  or <tt>null</tt> if initial sync has not yet been completed. */
    String getSyncToken() {
        return mSyncToken;
    }

    /** Returns the <tt>SyncResponse</tt> content from the pending initial
     *  sync, or <tt>null</tt> if initial sync is not currently in progress. */
    Element getInitialSyncResponse() {
        return mInitialSync;
    }

    /** Returns the id of the last item initial synced from the current folder
     *  during the pending initial sync, or <tt>0</tt> if initial sync is not
     *  currently in progress or if the initial sync of the previous folder
     *  completed. */
    int getLastSyncedItem() {
        return mLastSyncedItem;
    }

    /** Stores the <tt>SyncResponse</tt> content from the pending initial
     *  sync.  As a side effect, sets the mailbox's {@link SyncStage}
     *  to <tt>INITIAL</tt>. */
    void updateInitialSync(Element initial) throws ServiceException {
        updateInitialSync(initial, -1);
    }

    /** Stores the <tt>SyncResponse</tt> content from the pending initial
     *  sync.  As a side effect, sets the mailbox's {@link SyncStage}
     *  to <tt>INITIAL</tt>. */
    void updateInitialSync(Element initial, int lastId) throws ServiceException {
        if (initial == null)
            throw ServiceException.FAILURE("null Element passed to updateInitialSync", null);

        Metadata config = new Metadata().put(FN_PROGRESS, SyncStage.INITIAL).put(FN_INITIAL, initial).put(FN_LAST_ID, lastId);
        ombx.setConfig(null, SN_OFFLINE, config);

        setStage(SyncStage.INITIAL);
        mInitialSync = initial;
        mLastSyncedItem = lastId;
        mSyncToken = null;
    }

    /** Stores the sync token from the last completed sync (initial or
     *  delta).  As a side effect, sets the mailbox's {@link SyncStage}
     *  to <tt>SYNC</tt>. */
    void recordSyncComplete(String token) throws ServiceException {
        if (token == null)
            throw ServiceException.FAILURE("null sync token passed to recordSyncComplete", null);

        Metadata config = new Metadata().put(FN_PROGRESS, SyncStage.SYNC).put(FN_TOKEN, token);
        ombx.setConfig(null, SN_OFFLINE, config);

        setStage(SyncStage.SYNC);
        mSyncToken = token;
        mInitialSync = null;
    }
    
    private void setStage(SyncStage stage) throws ServiceException {
    	mStage = stage;
    	OfflineSyncManager.getInstance().setStage(ombx.getRemoteUser(), stage.toString());
    }
}
