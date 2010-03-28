/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbMailbox;
import com.zimbra.cs.db.DbOfflineMailbox;
import com.zimbra.cs.db.DbPool.Connection;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.redolog.op.DeleteMailbox;
import com.zimbra.cs.session.PendingModifications;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.store.StoreManager;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.cs.util.ZimbraApplication;

public abstract class SyncMailbox extends DesktopMailbox {
    static final String DELETING_MID_SUFFIX = ":delete";
    static final long OPTIMIZE_INTERVAL = 48 * Constants.MILLIS_PER_HOUR;

    private String accountName;
    private boolean isDeleting;

    private Timer timer;
    private TimerTask currentTask;

    Object syncLock = new Object();
    private boolean deleteAsync;
    private boolean mSyncRunning;
    private long lastOptimizeTime = 0;
    private static Long lastGC = new Long(0);

    public SyncMailbox(MailboxData data) throws ServiceException {
        super(data);

        OfflineAccount account = (OfflineAccount)getAccount();
        OfflineProvisioning provisioning = OfflineProvisioning.getOfflineInstance();
        
        if (account.isDataSourceAccount())
            accountName = account.getAttr(OfflineProvisioning.A_offlineDataSourceName);
        else
            accountName = account.getName();
        deleteAsync = !provisioning.isGalAccount(account) &&
            !provisioning.isMountpointAccount(account);
    }

    @Override
    protected synchronized void initialize() throws ServiceException {
        super.initialize();

        Folder userRoot = getFolderById(ID_FOLDER_USER_ROOT);
        
        Folder.create(ID_FOLDER_FAILURE, this, userRoot, FAILURE_PATH,
            Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, 0,
            MailItem.DEFAULT_COLOR_RGB, null, null);
        Folder.create(ID_FOLDER_OUTBOX, this, userRoot, OUTBOX_PATH,
            Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, 0,
            MailItem.DEFAULT_COLOR_RGB, null, null);
    }
    
    @Override
    synchronized boolean finishInitialization() throws ServiceException {
        if (super.finishInitialization()) {
            initSyncTimer();
            return true;
        }
        return false;
    }

    boolean lockMailboxToSync() {
        if (isDeleting() || !OfflineSyncManager.getInstance().isServiceActive())
            return false;
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

    void unlockMailbox() {
        assert mSyncRunning == true;
        mSyncRunning = false;
    }

    public boolean isDeleting() {
        return isDeleting;
    }

    public String getAccountName() {
        return accountName;
    }

    @Override
    public void deleteMailbox() throws ServiceException {
        deleteMailbox(deleteAsync);
    }

    public void deleteMailbox(boolean async) throws ServiceException {
        synchronized (this) {
            if (isDeleting)
                return;
            isDeleting = true;
            cancelCurrentTask();
            beginMaintenance(); // mailbox maintenance will cause sync to stop when writing
        }
        synchronized (syncLock) { // wait for any hot sync thread to unwind
            endMaintenance(true);
        }
        try {
            resetSyncStatus();
        } catch (ServiceException x) {
            if (!x.getCode().equals(AccountServiceException.NO_SUCH_ACCOUNT))
                OfflineLog.offline.warn(x);
        }
        if (async) {
            class DeleteThread extends Thread {
                SyncMailbox mbox;
                
                DeleteThread(SyncMailbox mbox) {
                    super("mailbox-reaper:" + mbox.getAccountId());
                    this.mbox = mbox;
                }
                public void run() {
                    try {
                        mbox.deleteThisMailbox(true);
                    } catch (Exception e) {
                        OfflineLog.offline.warn("unable to delete mailbox id " +
                            getId(), e);
                    }
                }
            }
            
            MailboxManager mm = MailboxManager.getInstance();
            
            synchronized (mm) {
                unhookMailboxForDeletion();
                mm.markMailboxDeleted(this); // to remove from cache
            }
            new DeleteThread(this).start();
        } else {
            deleteThisMailbox(false);
        }
    }
	
    private synchronized String unhookMailboxForDeletion()
        throws ServiceException {
        String accountId = getAccountId();
        boolean success = false;
        
        if (accountId.endsWith(DELETING_MID_SUFFIX))
            return accountId;
        accountId = accountId + ":" + getId() + DELETING_MID_SUFFIX;
        try {
            beginTransaction("replaceAccountId", null);
            DbOfflineMailbox.replaceAccountId(this, accountId);
            success = true;
            return accountId;
        } finally {
            endTransaction(success);
        }
    }

    void deleteThisMailbox(boolean async) throws ServiceException {
        OfflineLog.offline.info("deleting mailbox %s", getAccountId());
        if (async) {
            DeleteMailbox redoRecorder = new DeleteMailbox(getId());
            boolean success = false;
            
            synchronized(this) {
                try {
                    beginTransaction("deleteMailbox", null, redoRecorder);
                    redoRecorder.log();
    
                    Connection conn = getOperationConnection();
                    
                    DbMailbox.clearMailboxContent(this);
                    synchronized(MailboxManager.getInstance()) {
                        DbMailbox.deleteMailbox(conn, this);
                    }
                    success = true;
                } catch (Exception e) {
                    ZimbraLog.store.warn("Unable to delete mailbox data", e);
                } finally {
                    endTransaction(success);
                }
                try {
                    if (mIndexHelper != null)
                        mIndexHelper.deleteIndex();
                } catch (Exception e) {
                    ZimbraLog.store.warn("Unable to delete index data", e);
                }
                try {
                    StoreManager.getInstance().deleteStore(this);
                } catch (IOException e) {
                    ZimbraLog.store.warn("Unable to delete message data", e);
                }
            }
        } else {
            super.deleteMailbox();
        }
        OfflineLog.offline.info("mailbox %s deleted", getAccountId());
    }

    void resetSyncStatus() throws ServiceException {
        OfflineSyncManager.getInstance().resetStatus(getAccount());
        ((OfflineAccount)getAccount()).resetLastSyncTimestamp();
        OfflineYAuth.deleteRawAuthManager(this);
    }

    public synchronized void cancelCurrentTask() {
        if (currentTask != null)
            currentTask.cancel();
        currentTask = null;
    }

    protected synchronized void initSyncTimer() throws ServiceException {
        cancelCurrentTask();
        currentTask = new TimerTask() {
            public void run() {
                boolean doGC;
                long now;
                
                if (ZimbraApplication.getInstance().isShutdown()) {
                    cancelCurrentTask();
                    return;
                }
                try {
                    syncOnTimer();
                    now = System.currentTimeMillis();
                    if (lastOptimizeTime == 0) {
                    	lastOptimizeTime = now - OPTIMIZE_INTERVAL +
                    		30 * Constants.MILLIS_PER_MINUTE;
                    } else if (now - lastOptimizeTime > OPTIMIZE_INTERVAL) {
                        optimize(null, 0);
                        lastOptimizeTime = now;
                    }
                } catch (Throwable e) { // don't let exceptions kill the timer
                    if (e instanceof OutOfMemoryError)
                        Zimbra.halt("caught out of memory error", e);
                    else if (OfflineSyncManager.getInstance().isServiceActive())
                        OfflineLog.offline.warn("caught exception in timer ", e);
                }
                synchronized (lastGC) {
                    now = System.currentTimeMillis();
                    doGC = now - lastGC > 5 * 60 * Constants.MILLIS_PER_SECOND;
                    if (doGC) {
                        System.gc();
                        lastGC = now;
                    }
                }
            }
        };

        timer = new Timer("sync-mbox-" + getAccount().getName());
        timer.schedule(currentTask, 10 * Constants.MILLIS_PER_SECOND,
            5 * Constants.MILLIS_PER_SECOND);
    }

    protected abstract void syncOnTimer();

    public abstract void sync(boolean isOnRequest, boolean isDebugTraceOn)
        throws ServiceException;

    public abstract boolean isAutoSyncDisabled();

    @Override
    void snapshotCounts() throws ServiceException {
        // do the normal persisting of folder/tag counts
        super.snapshotCounts();

        boolean outboxed = false;
        
        PendingModifications pms = getPendingModifications();
        if (pms == null || !pms.hasNotifications())
            return;

        if (pms.created != null) {
            for (MailItem item : pms.created.values()) {
                if ((item.getId() >= FIRST_USER_ID || item instanceof Tag) && item.getFolderId() != ID_FOLDER_FAILURE) {
                	itemCreated(item);
                	trackChangeNew(item);
                    if (item.getFolderId() == ID_FOLDER_OUTBOX)
                    	outboxed = true;
                }
            }
        }

        if (pms.modified != null) {
            for (Change change : pms.modified.values()) {
                if (!(change.what instanceof MailItem))
                    continue;
                MailItem item = (MailItem) change.what;
                if ((item.getId() >= FIRST_USER_ID || item instanceof Tag) && item.getFolderId() != ID_FOLDER_FAILURE) {
                	trackChangeModified(item, change.why);
                    if (item.getFolderId() == ID_FOLDER_OUTBOX)
                        outboxed = true;
                }
            }
        }
        
        if (outboxed) {
            OutboxTracker.invalidate(this);
        }
    }

    void trackChangeNew(MailItem item) throws ServiceException {}

    void trackChangeModified(MailItem item, int changeMask) throws ServiceException {}

    void itemCreated(MailItem item) throws ServiceException {}
}
