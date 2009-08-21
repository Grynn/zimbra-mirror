package com.zimbra.cs.mailbox;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbOfflineMailbox;
import com.zimbra.cs.mailbox.util.TypedIdList;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.session.PendingModifications;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.cs.util.ZimbraApplication;

public abstract class SyncMailbox extends DesktopMailbox {

    static class DeletingMailbox extends SyncMailbox {
        DeletingMailbox(MailboxData data) throws ServiceException {
            super(data);
        }

        @Override
        synchronized boolean finishInitialization() {
            final String accountId = getAccountId();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(15000);
                        deleteThisMailbox();
                    } catch (Exception x) {
                        OfflineLog.offline.error("Deleting mailbox %s mailbox",
                            accountId, x);
                    }
                }
            }, "mailbox-reaper:" + accountId).start();
            return false;
        }

        @Override
        public boolean isAutoSyncDisabled() {
            return false;
        }

        @Override
        public void sync(boolean isOnRequest, boolean isDebugTraceOn)
            throws ServiceException {}

        @Override
        protected void syncOnTimer() {}
    }

    static final String DELETING_MID_SUFFIX = ":delete";

    private String accountName;
    private boolean isDeleting;

    private Timer timer;
    private TimerTask currentTask;
	
    Object syncLock = new Object();
    private boolean mSyncRunning;

    public SyncMailbox(MailboxData data) throws ServiceException {
        super(data);

        if (this instanceof DeletingMailbox) {
            accountName = getAccountId();
        } else {
            OfflineAccount account = (OfflineAccount)getAccount();
            if (account.isDataSourceAccount())
                accountName = account
                    .getAttr(OfflineProvisioning.A_offlineDataSourceName);
            else
                accountName = account.getName();
        }
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
            MailboxManager mm = MailboxManager.getInstance();
            synchronized (mm) {
                unhookMailboxForDeletion();
                mm.markMailboxDeleted(this); // to remove from cache
            }
            mm.getMailboxById(getId(), true); // the mailbox will now be loaded
                                              // as a DeletingMailbox
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
	
    /* NOTE: how we deal with archiving
     * 
     * all items in archive will have the \Archived flag set.  this flag will tell us if an item is moved in or out of archive at the end of a move transaction.
     * 
     * when an item is moved into archive, we'll flag it \Archived.
     * 
     * when an item is moved out of archive, we'll clear the \Archived flag.
     * 
     * regarding new items.  if an item is added in archive directly, we'll simply add \Archived flag.
     * 
     * Bug 32184: when moving items out of archive into Trash, don't clear the \Archived flag;
     * when moving items with \Archived flag into other non-archive folders, clear \Archived flag.
     * In other words we'll treat Trash as an extension of Local Folders.
     * 
     */

    @Override void snapshotCounts() throws ServiceException {
        // do the normal persisting of folder/tag counts
        super.snapshotCounts();

        boolean outboxed = false;
        
        PendingModifications pms = getPendingModifications();
        if (pms == null || !pms.hasNotifications())
            return;

        if (pms.created != null) {
            for (MailItem item : pms.created.values()) {
                if ((item.getId() >= FIRST_USER_ID || item instanceof Tag) && item.getFolderId() != ID_FOLDER_FAILURE) {
                    if (isInArchive(item.getPath())) {//new item created or imported into archive
                    	alterArchivedFlag(item, true);
                    	itemCreated(item, true);
                    } else {
                    	itemCreated(item, false);
                    	trackChangeNew(item);
                        if (item.getFolderId() == ID_FOLDER_OUTBOX)
                        	outboxed = true;
                    }
                }
            }
        }

        if (pms.modified != null) {
            for (Change change : pms.modified.values()) {
                if (!(change.what instanceof MailItem))
                    continue;
                MailItem item = (MailItem) change.what;
                if ((item.getId() >= FIRST_USER_ID || item instanceof Tag) && item.getFolderId() != ID_FOLDER_FAILURE) {
                    String path = item.getPath();
                    boolean isInArchive = isInArchive(path);
                    boolean isInTrash = isInTrash(path);
                    if (!isInArchive && !isInTrash || !item.isTagged(Flag.ID_FLAG_ARCHIVED)) { //either not in archive/trash, or newly archived, we need to keep track
                	trackChangeModified(item, change.why);
                        if (item.getFolderId() == ID_FOLDER_OUTBOX)
                            outboxed = true;
                	}
                    
                    if ((change.why & Change.MODIFIED_FOLDER) != 0) {
                        if (isInArchive && !item.isTagged(Flag.ID_FLAG_ARCHIVED)) //moved into archive
                            archive(item, true, false);
                        else if (!isInArchive && item.isTagged(Flag.ID_FLAG_ARCHIVED)) //moved out of archive
                            archive(item, false, isInTrash);
                    }
                }
            }
        }
        if (outboxed) {
            OutboxTracker.invalidate(this);
        }
    }
    
    private void alterArchivedFlag(MailItem item, boolean toArchive) throws ServiceException {
    	// alter \Archived flag, but don't use MailItem.alterSystemFlag() since that would insert more changes into PendingModifications
    	// we are currently looping through.  in any case we don't need to keep track of this particular flag change.
        Flag archivedFlag = getFlagById(Flag.ID_FLAG_ARCHIVED);

        DbMailItem.alterTag(archivedFlag, Arrays.asList(item.getId()),
            toArchive);
        if (toArchive)
            item.mData.flags |= archivedFlag.getBitmask();
        else
            item.mData.flags &= ~archivedFlag.getBitmask();
    }

    /**
     * An item has been moved into or out of archive.  We'll set or clear \Archive flag, and will set or clear dirty bits accordingly.
     * If the item is a folder, we do the same to all its subfolders and leaf items.
     * 
     * @param item
     * @param toArchive true to move into archive; false to move out of
     * @throws ServiceException
     */
    private void archive(MailItem item, boolean toArchive, boolean isTrashing)
        throws ServiceException {
        if (item instanceof Folder) {
            TypedIdList ids = DbMailItem.listByFolder((Folder)item, true);
            for (byte type : ids.types()) {
                MailItem[] items = getItemById(ids.getIds(type), type);
                for (MailItem i : items) {
                    if (type == MailItem.TYPE_FOLDER)
                        archive(i, toArchive, isTrashing);
                    else
                        archiveSingleItem(i, toArchive, isTrashing);
                }
            }
        }
        archiveSingleItem(item, toArchive, isTrashing);
    }

    void archiveSingleItem(MailItem item, boolean toArchive, boolean isTrashing)
        throws ServiceException {
        if (trackChangeArchived(item, toArchive, isTrashing))
            alterArchivedFlag(item, toArchive);
    }

    public static boolean isInArchive(String path) {
        return path.startsWith("/" + ARCHIVE_PATH);
    }

    public static boolean isInTrash(String path) {
        return path.startsWith("/Trash");
    }

    boolean isItemInArchive(MailItem item) {
        return (item.getInternalFlagBitmask() & Flag.BITMASK_ARCHIVED) != 0;
    }

    void trackChangeNew(MailItem item) throws ServiceException {}

    void trackChangeModified(MailItem item, int changeMask) throws ServiceException {}

    boolean trackChangeArchived(MailItem item, boolean toArchive, boolean isTrashing) throws ServiceException { return true; }

    void itemCreated(MailItem item, boolean inArchive) throws ServiceException {}
}
