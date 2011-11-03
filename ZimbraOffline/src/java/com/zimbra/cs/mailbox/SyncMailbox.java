/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010, 2011 Zimbra, Inc.
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.primitives.Ints;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbMailbox;
import com.zimbra.cs.db.DbOfflineMailbox;
import com.zimbra.cs.db.DbPool.DbConnection;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mailbox.util.TypedIdList;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.common.OfflineConstants.SyncMsgOptions;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.redolog.op.DeleteItem;
import com.zimbra.cs.redolog.op.DeleteMailbox;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.session.PendingModifications;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.session.PendingModifications.ModificationKey;
import com.zimbra.cs.store.StoreManager;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.cs.util.ZimbraApplication;

public abstract class SyncMailbox extends DesktopMailbox {
    static final String DELETING_MID_SUFFIX = ":delete";
    static final long OPTIMIZE_INTERVAL = 48 * Constants.MILLIS_PER_HOUR;
    static final int FIFTEEN_MIN = 15;
    private String accountName;
    private volatile boolean isDeleting;

    private Timer timer;
    private TimerTask currentTask;

    private Timer syncMaildeltimer;
    private TimerTask syncMaildelcurrentTask;

    final Object syncLock = new Object();
    private boolean deleteAsync;
    private boolean mSyncRunning;
    private boolean isGalAcct;
    private boolean isMPAcct;
    private long lastOptimizeTime = 0;
    private static final AtomicLong lastGC = new AtomicLong();
    private Set<Long> syncedIds = new HashSet<Long>();

    public int getSyncCount() {
        return syncedIds.size();
    }

    public void recordItemSync(long itemId) {
        syncedIds.add(itemId); //using set rather than a counter since various call sites may touch the same item more than once
    }

    public void resetSyncCounter() {
        syncedIds.clear();
    }

    public SyncMailbox(MailboxData data) throws ServiceException {
        super(data);

        OfflineAccount account = (OfflineAccount)getAccount();
        OfflineProvisioning provisioning = OfflineProvisioning.getOfflineInstance();

        if (account.isDataSourceAccount())
            accountName = account.getAttr(OfflineProvisioning.A_offlineDataSourceName);
        else
            accountName = account.getName();
        isGalAcct = provisioning.isGalAccount(account);
        isMPAcct = provisioning.isMountpointAccount(account);
        deleteAsync = !isGalAcct && !isMPAcct;
    }

    @Override
    protected void initialize() throws ServiceException {
        lock.lock();
        try {
            super.initialize();

            Folder userRoot = getFolderById(ID_FOLDER_USER_ROOT);

            Folder.create(ID_FOLDER_FAILURE, this, userRoot, FAILURE_PATH,
                Folder.FOLDER_IS_IMMUTABLE, MailItem.Type.MESSAGE, 0,
                MailItem.DEFAULT_COLOR_RGB, null, null);
            Folder.create(ID_FOLDER_OUTBOX, this, userRoot, OUTBOX_PATH,
                Folder.FOLDER_IS_IMMUTABLE, MailItem.Type.MESSAGE, 0,
                MailItem.DEFAULT_COLOR_RGB, null, null);
        } finally {
            lock.release();
        }
    }

    @Override
    boolean open() throws ServiceException {
        if (super.open()) {
            initSyncTimer();
            return true;
        }
        return false;
    }

    boolean lockMailboxToSync() {
        if (isDeleting() || !OfflineSyncManager.getInstance().isServiceActive(false)) {
            return false;
        }
        if (!mSyncRunning) {
            lock.lock();
            try {
                if (!mSyncRunning) {
                    mSyncRunning = true;
                    return true;
                }
            } finally {
                lock.release();
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
        lock.lock();
        try {
            if (isDeleting) {
                return;
            }
            isDeleting = true;
            cancelCurrentTask();
            beginMaintenance(); // mailbox maintenance will cause sync to stop when writing
        } finally {
            lock.release();
        }
        synchronized (syncLock) { // wait for any hot sync thread to unwind
            endMaintenance(true);
        }
        try {
            resetSyncStatus();
        } catch (ServiceException x) {
            if (!x.getCode().equals(AccountServiceException.NO_SUCH_ACCOUNT)) {
                OfflineLog.offline.warn(x);
            }
        }

        MailboxManager mm = MailboxManager.getInstance();

        synchronized (mm) {
            unhookMailboxForDeletion();
            mm.markMailboxDeleted(this); // to remove from cache
        }
        deleteThisMailbox();
    }

    private String unhookMailboxForDeletion() throws ServiceException {
        String accountId = getAccountId();
        boolean success = false;

        if (accountId.endsWith(DELETING_MID_SUFFIX)) {
            return accountId;
        }
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

    void deleteThisMailbox() throws ServiceException {
        OfflineLog.offline.info("deleting mailbox %s %s (%s)", getId(), getAccountId(), getAccountName());
        DeleteMailbox redoRecorder = new DeleteMailbox(getId());
        boolean success = false;

        lock.lock();
        try {
            try {
                beginTransaction("deleteMailbox", null, redoRecorder);
                redoRecorder.log();

                DbConnection conn = getOperationConnection();

                synchronized(MailboxManager.getInstance()) {
                    DbMailbox.deleteMailbox(conn, this);
                }
                DbMailbox.clearMailboxContent(conn, this);

                success = true;
            } catch (Exception e) {
                ZimbraLog.store.warn("Unable to delete mailbox data", e);
            } finally {
                endTransaction(success);
            }
            try {
                index.deleteIndex();
            } catch (Exception e) {
                ZimbraLog.store.warn("Unable to delete index data", e);
            }
            try {
                StoreManager.getInstance().deleteStore(this);
            } catch (IOException e) {
                ZimbraLog.store.warn("Unable to delete message data", e);
            }
        } finally {
            lock.release();
        }
        OfflineLog.offline.info("mailbox %s (%s) deleted", getAccountId(), getAccountName());
    }

    void resetSyncStatus() throws ServiceException {
        OfflineSyncManager.getInstance().resetStatus(getAccount());
        ((OfflineAccount)getAccount()).resetLastSyncTimestamp();
        OfflineYAuth.deleteRawAuthManager(this);
    }

    public void cancelCurrentTask() {
        lock.lock();
        try {
            if (currentTask != null) {
                currentTask.cancel();
            }
            if (syncMaildelcurrentTask != null) {
                syncMaildelcurrentTask.cancel();
            }
            currentTask = null; syncMaildelcurrentTask = null;
        } finally {
            lock.release();
        }
    }

    protected void initSyncTimer() throws ServiceException {
        lock.lock();
        try {
            if (isGalAcct || isMPAcct) {
                return;
            }
            cancelCurrentTask();
            currentTask = new TimerTask() {
                @Override
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
                            lastOptimizeTime = now - OPTIMIZE_INTERVAL + 30 * Constants.MILLIS_PER_MINUTE;
                        } else if (now - lastOptimizeTime > OPTIMIZE_INTERVAL) {
                            optimize(0);
                            lastOptimizeTime = now;
                        }
                    } catch (Throwable e) { // don't let exceptions kill the timer
                        if (e instanceof OutOfMemoryError) {
                            Zimbra.halt("caught out of memory error", e);
                        } else if (OfflineSyncManager.getInstance().isServiceActive(false)) {
                            OfflineLog.offline.warn("caught exception in timer ", e);
                        }
                    }
                    synchronized (lastGC) {
                        now = System.currentTimeMillis();
                        doGC = now - lastGC.get() > 5 * 60 * Constants.MILLIS_PER_SECOND;
                        if (doGC) {
                            System.gc();
                            lastGC.set(now);
                        }
                    }
                }
            };

	    timer = new Timer("sync-mbox-" + getAccount().getName());
       	    timer.schedule(currentTask, 10 * Constants.MILLIS_PER_SECOND, 5 * Constants.MILLIS_PER_SECOND);

        syncMaildelcurrentTask = new TimerTask() {
            public void run() {

                if (ZimbraApplication.getInstance().isShutdown()) {
                    cancelCurrentTask();
                    return;
                }

                OfflineAccount account = null;
                try {
                    account = (OfflineAccount)getAccount();
                } catch (ServiceException e) {
                    ZimbraLog.store.warn("Failed to retrive the account", e);
                }

                long cutoffTime = 0L;
                try {
                    switch (SyncMsgOptions.getOption(account.getAttr(OfflineConstants.A_offlinesyncEmailDate))) {
                    case SYNCTORELATIVEDATE :
                        //task is performed only if the sync is set to a relative date
                        cutoffTime = Long.parseLong(InitialSync.convertRelativeDatetoLong(account.getAttr(OfflineConstants.A_offlinesyncRelativeDate) ,
                                account.getAttr(OfflineConstants.A_offlinesyncFieldName)));
                        if (cutoffTime > 0) {
                            OfflineLog.offline.info("deleting messages from %s older than %d", getAccountName(), cutoffTime);
                            Folder root = null;
                            Set<Folder> visible = null;
                            try {
                                OperationContext octxtOwner = new OperationContext(SyncMailbox.this);
                                root = getFolderById(octxtOwner, Mailbox.ID_FOLDER_ROOT);
                                deleteMsginFolder(cutoffTime, root, visible);
                            } catch (ServiceException e) {
                                OfflineLog.offline.info("error in (%s) while deleting stale messages", getAccountName());
                            }
                            OfflineLog.offline.info("message in (%s) are deleted", getAccountName());
                        }
                        break;
                    }
                } catch (NumberFormatException x) {
                    OfflineLog.offline.warn("unable to parse syncEmailDate", x);
                }
            }
        };

        syncMaildeltimer = new Timer("delEmail" + getAccount().getName());

        Calendar threadRunTime = GregorianCalendar.getInstance();
        threadRunTime.add(Calendar.MINUTE, FIFTEEN_MIN);
        syncMaildeltimer.scheduleAtFixedRate(syncMaildelcurrentTask, threadRunTime.getTime(), Constants.MILLIS_PER_DAY);
        } finally {
            lock.release();
        }
    }

    public boolean deleteMsginFolder(long cutoffTime, Folder folder, Set<Folder> visible)
        throws ServiceException {

            if (folder == null)
                return false;

            if (visible != null && visible.isEmpty())
                return false;
            boolean isVisible = visible == null || visible.remove(folder);

            List<Folder> subfolders = folder.getSubfolders(null);
            if (!isVisible && subfolders.isEmpty())
                return false;

            if (isVisible && folder.getType() == MailItem.Type.FOLDER) {
                if (folder.getId() != Mailbox.ID_FOLDER_TAGS) {
                    TypedIdList idlist;
                    boolean success = false;
                    synchronized (this) {
                        try {
                            beginTransaction("listMessageItemsforgivenDate", getOperationContext());
                            idlist = DbMailItem.listMsgItems(folder, cutoffTime, true, true);
                            success = true;
                        } finally {
                            endTransaction(success);
                        }
                    }

                    List<Integer> items = idlist.getIds(MailItem.Type.MESSAGE);
                    if (items != null && !items.isEmpty()) {
                        for (int id : items) {
                            MailItem item;
                            success = false;
                            DeleteItem redoRecorder = new DeleteItem(this.getId(), Ints.toArray(items), MailItem.Type.MESSAGE, getOperationTargetConstraint());
                            synchronized (this) {
                                try {
                                    beginTransaction("delete", getOperationContext(), redoRecorder);
                                    try {
                                        item = getItemById(id, MailItem.Type.UNKNOWN);
                                        item.delete(false);
                                    } catch (NoSuchItemException nsie) {
                                        continue;
                                    }
                                    success = true;
                                }finally {
                                    endTransaction(success);
                                }
                            }
                        }
                    }
                }
            }

            if (isVisible && visible != null && visible.isEmpty())
                return true;

            for (Folder subfolder : subfolders) {
                if (subfolder != null)
                    isVisible |= deleteMsginFolder(cutoffTime, subfolder, visible);
            }
            return isVisible;
    }

    protected abstract void syncOnTimer();

    public abstract void sync(boolean isOnRequest, boolean isDebugTraceOn) throws ServiceException;

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
                    if (item.getFolderId() == ID_FOLDER_OUTBOX) {
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
                    trackChangeModified(item, change.why);
                    if (item.getFolderId() == ID_FOLDER_OUTBOX) {
                        outboxed = true;
                    }
                }
            }
        }
        if (pms.deleted != null) {
            for (ModificationKey key : pms.deleted.keySet()) {
                if (key.getItemId() >= FIRST_USER_ID) {
                    trackChangeDeleted();
                }
            }
        }
        if (outboxed) {
            OutboxTracker.invalidate(this);
        }
    }

    void trackChangeNew(MailItem item) throws ServiceException {}

    void trackChangeModified(MailItem item, int changeMask) throws ServiceException {}

    void trackChangeDeleted() throws ServiceException {}

    void itemCreated(MailItem item) throws ServiceException {}
}
