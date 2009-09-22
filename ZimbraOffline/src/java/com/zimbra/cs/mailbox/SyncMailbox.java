package com.zimbra.cs.mailbox;

import java.util.Timer;
import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbOfflineMailbox;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.session.PendingModifications;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.cs.util.ZimbraApplication;

public abstract class SyncMailbox extends DesktopMailbox {

    static final String DELETING_MID_SUFFIX = ":delete";

    private String accountName;
    private boolean isDeleting;

    private Timer timer;
    private TimerTask currentTask;
	
    Object syncLock = new Object();
    private boolean mSyncRunning;

    public SyncMailbox(MailboxData data) throws ServiceException {
        super(data);

        OfflineAccount account = (OfflineAccount)getAccount();
        if (account.isDataSourceAccount())
            accountName = account.getAttr(OfflineProvisioning.A_offlineDataSourceName);
        else
            accountName = account.getName();
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
        if (isDeleting() || !OfflineSyncManager.getInstance().isServiceOpen()
            || OfflineSyncManager.getInstance().isUiLoadingInProgress())
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
        deleteMailbox(true);
    }

    public void deleteMailbox(boolean asynch) throws ServiceException {
        synchronized (this) {
            if (isDeleting)
                return;
            isDeleting = true;

            cancelCurrentTask();

            beginMaintenance(); // putting mailbox in maintenance will cause
                                // sync to stop when writing
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

        if (asynch) {
            class DeleteThread extends Thread {
                private long id;
                
                DeleteThread(long id, String accountId) {
                    super("mailbox-reaper:" + accountId);
                    this.id = id;
                }
                public void run() {
                    try {
                        Thread.sleep(5000);
                        
                        MailboxManager mgr = MailboxManager.getInstance();
                        SyncMailbox mbox = (SyncMailbox)mgr.getMailboxById(id,
                            true);
                        
                        synchronized (mgr) {
                            if (mbox != null)
                                mbox.deleteThisMailbox();
                        }
                    } catch (Exception e) {
                        OfflineLog.offline.warn("unable to delete mailbox id " +
                            id, e);
                    }
                }
            }
            
            MailboxManager mm = MailboxManager.getInstance();
            
            synchronized (mm) {
                unhookMailboxForDeletion();
                mm.markMailboxDeleted(this); // to remove from cache
            }
            new DeleteThread(getId(), getAccountId()).start();
        } else {
            deleteThisMailbox();
        }
    }
	
    private synchronized String unhookMailboxForDeletion()
        throws ServiceException {
        String accountId = getAccountId();
        if (accountId.endsWith(DELETING_MID_SUFFIX))
            return accountId;

        accountId = accountId + ":" + getId() + DELETING_MID_SUFFIX;
        boolean success = false;
        try {
            beginTransaction("replaceAccountId", null);
            DbOfflineMailbox.replaceAccountId(this, accountId);
            success = true;
            return accountId;
        } finally {
            endTransaction(success);
        }
    }

    void deleteThisMailbox() throws ServiceException {
        OfflineLog.offline.info("deleting mailbox %s", getAccountId());
        super.deleteMailbox();
        OfflineLog.offline.info("mailbox %s deleted", getAccountId());
    }

    void resetSyncStatus() throws ServiceException {
        OfflineSyncManager.getInstance().resetStatus(accountName);
        ((OfflineAccount)getAccount()).resetLastSyncTimestamp();
        OfflineYAuth.deleteRawAuthManager(this);
    }

    public synchronized void cancelCurrentTask() {
        if (currentTask != null)
            currentTask.cancel();
        currentTask = null;
    }

    protected synchronized void initSyncTimer() throws ServiceException {
        if (((OfflineAccount)getAccount()).isLocalAccount())
            return;

        cancelCurrentTask();

        currentTask = new TimerTask() {
            public void run() {
                if (ZimbraApplication.getInstance().isShutdown())
                    return;
                try {
                    syncOnTimer();
                } catch (Throwable e) { // don't let exceptions kill the timer
                    if (e instanceof OutOfMemoryError)
                        Zimbra.halt("Caught out of memory error", e);
                    OfflineLog.offline.warn("Caught exception in timer ", e);
                }
            }
        };

        timer = new Timer("mid=" + getId());
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
