/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;

public class MailboxSync {
    
	//legacy
    private static final String SN_OFFLINE  = "offline";
    private static final String FN_PROGRESS = "state";
    private static final String FN_TOKEN    = "token";
    private static final String FN_INITIAL  = "initial";
    private static final String FN_LAST_ID  = "last";
    
    //SyncResponse tree, only used during initial sync
    //because this could be huge, we only ever write it once to derby CLOB
    private static final String CONF_SYNCTREE = "synctree";
    private static final String CKEY_SYNCRESP = "syncresp";
    
    //status, used during both initial and incremental stage
    private static final String CONF_SYNCSTATE = "syncstate";
    private static final String CKEY_STAGE = "stage";
    private static final String CKEY_DONE_FOLDERS = "done"; //list of completed folders, only for initial sync
    private static final String CKEY_LASTID = "lastid"; //last checkpoint, only for initial sync
    private static final String CKEY_TOKEN = "token"; //last sync token, only for incremental sync
    
    private enum SyncStage {
        BLANK, INITIAL, SYNC
    }
	
    private Element mSyncTree;
    
    private SyncStage mStage = SyncStage.BLANK;
    private Set<Long> mDoneFolders = new HashSet<Long>();
    private int mLastSyncedItem;
    private String mSyncToken; //during initial sync, this token is set to the peek-forward delta token.  it's reset to initial token at the end of initial sync
    
    private ZcsMailbox ombx;
    
    private OfflinePoller poller;

    @SuppressWarnings("unchecked")
    MailboxSync(ZcsMailbox ombx) throws ServiceException {
    	this.ombx = ombx;
    	poller = new OfflinePoller(ombx);
    	
    	Metadata syncState = ombx.getConfig(null, CONF_SYNCSTATE);
    	if (syncState != null && syncState.containsKey(CKEY_STAGE)) {
            try {
            	setStage(SyncStage.valueOf(syncState.get(CKEY_STAGE)));
                switch (mStage) {
                case INITIAL: {
                    MetadataList mdl = syncState.getList(CKEY_DONE_FOLDERS, true);
                    if (mdl != null)
                        mDoneFolders.addAll(mdl.asList());
                    mLastSyncedItem = (int)syncState.getLong(CKEY_LASTID, 0);
                    	
                    Metadata syncTree = ombx.getConfig(null, CONF_SYNCTREE);
                    mSyncTree = Element.parseXML(syncTree.get(CKEY_SYNCRESP));
                    //fall-thru
                }
                case SYNC: {
                    mSyncToken = syncState.get(CKEY_TOKEN, null);
                    break;
                }
                }
            } catch (Exception e) {
                ZimbraLog.mailbox.warn("invalid persisted sync data - must reset mailbox", e);
            }
    	} else { //legacy metadata support
    	    Metadata config = ombx.getConfig(null, SN_OFFLINE);
    	    if (config != null && config.containsKey(FN_PROGRESS)) {
    	        try {
    	            setStage(SyncStage.valueOf(config.get(FN_PROGRESS)));
    	            switch (mStage) {
    	            case INITIAL: {
    	                Element syncTree = Element.parseXML(config.get(FN_INITIAL));
    	                int lastId = (int) config.getLong(FN_LAST_ID, 0);
    	                saveSyncTree(syncTree, syncTree.getAttribute(MailConstants.A_TOKEN));
    	                checkpointItem(lastId);
    	                break;
    	            }
    	            case SYNC: {
    	                String token = config.get(FN_TOKEN, null);
    	                recordInitialSyncComplete(token);
    	                break;
    	            }
    	            }
    	            ombx.setConfig(null, SN_OFFLINE, null);
    	        } catch (Exception e) {
    	            ZimbraLog.mailbox.warn("invalid persisted sync data; will force reset");
	        }
    	    }
    	}
    }
    
    void sync(boolean isOnRequest, boolean isDebugTraceOn) throws ServiceException {
       	OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
       	
        if (!syncMan.isServiceActive(isOnRequest)) {
            if (isOnRequest)
                OfflineLog.offline.debug("offline sync request ignored");
        } else if (ombx.lockMailboxToSync()) {
            synchronized (ombx.syncLock) {
                if (isOnRequest && isDebugTraceOn) {
                    OfflineLog.offline.debug("============================== SYNC DEBUG TRACE START ==============================");
                    ombx.getOfflineAccount().setRequestScopeDebugTraceOn(true);
        	}
                try {
                    if (!isOnRequest) {
                        if (ombx.isAutoSyncDisabled() || !syncMan.reauthOK(ombx.getAccount()) || !syncMan.retryOK(ombx.getAccount()))
                            return;
                    }

                    boolean forceSync = false;
                    if (mStage == SyncStage.SYNC) {
                        int totalSent = PushChanges.sendPendingMessages(ombx, isOnRequest);
                        if (totalSent > 0)
                            forceSync = true;
                        else
                            syncMan.syncComplete(ombx.getAccount()); //sendPendingMessages may have called syncStart but then send fails
                    }
                    if (!forceSync && !isOnRequest) {
                        if (mStage == SyncStage.SYNC) {
                            long freqLimit = syncMan.getSyncFrequencyLimit();
                            long frequency = ombx.getSyncFrequency() < freqLimit ? freqLimit : ombx.getSyncFrequency();

                            if (freqLimit == 0 && syncMan.isOnLine(ombx.getAccount()) && ombx.isPushEnabled()) {
                                if (!poller.hasChanges(mSyncToken)) {
                                    if (ombx.anyChangesSince(syncMan.getLastSyncTime(ombx.getAccount()))) {
                                        syncMan.syncStart(ombx.getAccount());
                                        PushChanges.sync(ombx, isOnRequest);
                                        syncMan.syncComplete(ombx.getAccount());
                                    }
                                    return;
                                }
                            } else if (System.currentTimeMillis() - syncMan.getLastSyncTime(ombx.getAccount()) < frequency) {
                                return;
                            }
                        }
                    }
                    OfflineLog.offline.info(">>>>>>>> name=%s;version=%s;build=%s;release=%s;os=%s;server=%s",
                        ombx.getAccount().getName(), OfflineLC.zdesktop_version.value(),
                        OfflineLC.zdesktop_buildid.value(), OfflineLC.zdesktop_relabel.value(),
                        System.getProperty("os.name") + " " + System.getProperty("os.arch") +
                        " " + System.getProperty("os.version"),
                        ombx.getOfflineAccount().getRemoteServerVersion());
                    syncMan.syncStart(ombx.getAccount());
                    if (mStage == SyncStage.BLANK)
                        InitialSync.sync(ombx);
                    else if (mStage == SyncStage.INITIAL)
                        InitialSync.resume(ombx);
                    DeltaSync.sync(ombx);
                    if (PushChanges.sync(ombx, isOnRequest))
                        DeltaSync.sync(ombx);
                    syncMan.syncComplete(ombx.getAccount());
                    OfflineProvisioning.getOfflineInstance().setAccountAttribute(
                        ombx.getAccount(), OfflineConstants.A_offlineLastSync,
                        Long.toString(System.currentTimeMillis()));
                    GalSync.sync(ombx, isOnRequest);
                    syncMan.setConnectionDown(false);
                } catch (Exception e) {
                    if (!syncMan.isServiceActive()) {
                        return;
                    } else if (ombx.isDeleting()) {
                        OfflineLog.offline.info("Mailbox \"%s\" is being deleted", ombx.getAccountName());
                    } else if (e instanceof ServiceException && ((ServiceException)e).getCode().equals(ServiceException.AUTH_EXPIRED)) {
                        syncMan.clearAuthToken(ombx.getAccount());
                        throw (ServiceException)e;
                    } else {
                        syncMan.processSyncException(ombx.getAccount(), e);
                    }
                } catch (Error e) {
                    syncMan.processSyncError(ombx.getAccount(), e);
                } finally {
                    if (isOnRequest && isDebugTraceOn) {
                        ombx.getOfflineAccount().setRequestScopeDebugTraceOn(false);
                        OfflineLog.offline.debug("============================== SYNC DEBUG TRACE END ================================");
                    }
                    ombx.unlockMailbox();
                }
            }
        } else if (isOnRequest) {
            OfflineLog.offline.info("sync already in progress");
        }
    }
    
    /** Returns the sync token from the last completed initial or delta sync,
     *  or <tt>null</tt> if initial sync has not yet been completed. */
    String getSyncToken() {
        return mSyncToken;
    }

    /** Returns the <tt>SyncResponse</tt> content from the pending initial
     *  sync, or <tt>null</tt> if initial sync is not currently in progress. */
    Element getSyncTree() {
        return mSyncTree;
    }
    
    /**
     * Check if folder sync is done, only used during initial sync
     * @param folderId
     * @return
     */
    boolean isFolderDone(int folderId) {
    	return mDoneFolders.contains((long)folderId);
    }

    /** Returns the id of the last item initial synced from the current folder
     *  during the pending initial sync, or <tt>0</tt> if initial sync is not
     *  currently in progress or if the initial sync of the previous folder
     *  completed. */
    int getLastSyncedItem() {
        return mLastSyncedItem;
    }
    
    boolean isInitialSyncComplete() {
    	return mStage == SyncStage.SYNC;
    }
    
    /**
     * Store initial sync response, only ever called once
     * @param syncResponse
     * @param token token from initial sync response, which is the base for peek-forward delta
     * @throws ServiceException
     */
    void saveSyncTree(Element syncResponse, String token) throws ServiceException {
    	Metadata syncTree = new Metadata().put(CKEY_SYNCRESP, syncResponse);
    	ombx.setConfig(null, CONF_SYNCTREE, syncTree);
    	
    	setStage(SyncStage.INITIAL);
    	mSyncTree = syncResponse;
    	mDoneFolders = new HashSet<Long>();
    	mLastSyncedItem = 0;
    	mSyncToken = token;
    	checkpoint();
    }
    
    /**
     * Checkpoint last sync ID, only used during initial sync
     * @param itemId last synced itemId
     * @throws ServiceException
     */
    void checkpointItem(int itemId) throws ServiceException {
    	mLastSyncedItem = itemId;
    	checkpoint();
    }
    
    /**
     * Checkpoint a completed folder, only used during initial sync
     * @param folderId completed folder
     * @throws ServiceException
     */
    void checkpointFolder(int folderId) throws ServiceException {
    	mDoneFolders.add((long)folderId);
    	mLastSyncedItem = 0;
    	checkpoint();
    }
    
    private void checkpoint() throws ServiceException {
        Metadata syncState = new Metadata().put(CKEY_STAGE, SyncStage.INITIAL);
        
        if (mSyncToken != null)
            syncState.put(CKEY_TOKEN, mSyncToken);
        if (mDoneFolders.size() > 0)
            syncState.put(CKEY_DONE_FOLDERS, new MetadataList(
                new ArrayList<Long>(mDoneFolders)));
        if (mLastSyncedItem > 0)
            syncState.put(CKEY_LASTID, mLastSyncedItem);
        ombx.setConfig(null, CONF_SYNCSTATE, syncState);
    }
    
    
    /** Stores the sync token from initial sync.
     *  As a side effect, sets the mailbox's {@link SyncStage}
     *  to <tt>SYNC</tt>.
     */
    void recordInitialSyncComplete(String token) throws ServiceException {
        setStage(SyncStage.SYNC);
    	
        if (mSyncTree != null) {
            mSyncTree = null;
            ombx.setConfig(null, CONF_SYNCTREE, null);
        }
        mDoneFolders.clear();
        mLastSyncedItem = 0;
        recordSyncComplete(token);
    }

    /**
     * Stores the sync token from delta sync.
     * @param token
     * @throws ServiceException
     */
    void recordSyncComplete(String token) throws ServiceException {
        if (token == null)
            throw ServiceException.FAILURE("null sync token passed to recordSyncComplete", null);
        mSyncToken = token;
        if (mStage == SyncStage.SYNC)
            ombx.setConfig(null, CONF_SYNCSTATE, new Metadata().put(CKEY_STAGE,
                SyncStage.SYNC).put(CKEY_TOKEN, token));
        else
            checkpoint(); //called by completion of peek-forward delta
    }
    
    private void setStage(SyncStage stage) throws ServiceException {
    	mStage = stage;
    	OfflineSyncManager.getInstance().setStage(ombx.getAccount(), stage.toString());
    }
}
