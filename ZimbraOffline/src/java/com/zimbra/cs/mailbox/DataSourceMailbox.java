package com.zimbra.cs.mailbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Pair;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;

public class DataSourceMailbox extends SyncMailbox {
    private boolean hasFolders;
    private boolean isFlat;

    private final Flag mSyncFlag;
    private final Flag mSyncFolderFlag;
    private final Flag mNoInferiorsFlag;

    DataSourceMailbox(MailboxData data) throws ServiceException {
        super(data);

        OfflineDataSource ds = (OfflineDataSource)OfflineProvisioning.
            getOfflineInstance().getDataSource(getAccount());
        if (ds != null) {
            hasFolders = ds.getType() == DataSource.Type.imap ||
                ds.getType() == DataSource.Type.live;
            isFlat = ds.isLive() || ds.isYahoo();
        }
        mSyncFlag = getFlagById(Flag.ID_FLAG_SYNC);
        mSyncFolderFlag = getFlagById(Flag.ID_FLAG_SYNCFOLDER);
        mNoInferiorsFlag = getFlagById(Flag.ID_FLAG_NO_INFERIORS);
    }

    @Override protected synchronized void initialize() throws ServiceException {
        super.initialize();
        if (hasFolders) {
            List<Pair<Integer, String>> systemMailFolders = new ArrayList<
                Pair<Integer, String>>();
            
            systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_INBOX, "/Inbox"));
            systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_TRASH, "/Trash"));
            systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_SPAM,  "/Junk"));
            systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_SENT,  "/Sent"));
            // systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_DRAFTS, "/Drafts"));
            for (Pair<Integer, String> pair : systemMailFolders) {
                MailItem mi = getCachedItem(pair.getFirst());
                DbMailItem.alterTag(mSyncFolderFlag, Arrays.asList(pair.getFirst()), true);
                if (mi != null)
                    mi.mData.flags |= mSyncFolderFlag.getBitmask();
                if (isSyncEnabledByDefault(pair.getSecond())) {
                    DbMailItem.alterTag(mSyncFlag, Arrays.asList(pair.getFirst()), true);
                    if (mi != null)
                        mi.mData.flags |= mSyncFlag.getBitmask();
                }
            }
            if (isFlat) {
                DbMailItem.alterTag(mNoInferiorsFlag, Arrays.asList(
                    ID_FOLDER_INBOX, ID_FOLDER_TRASH, ID_FOLDER_SENT), true);
                MailItem mi = getCachedItem(ID_FOLDER_INBOX);
                if (mi != null)
                    mi.mData.flags |= mNoInferiorsFlag.getBitmask();
                mi = getCachedItem(ID_FOLDER_TRASH);
                if (mi != null)
                    mi.mData.flags |= mNoInferiorsFlag.getBitmask();
                mi = getCachedItem(ID_FOLDER_SENT);
                if (mi != null)
                    mi.mData.flags |= mNoInferiorsFlag.getBitmask();
            }
        }
    }

    @Override
    synchronized boolean finishInitialization() throws ServiceException {
        if (super.finishInitialization()) {
            if (hasFolders) {
                Folder draft = getFolderById(ID_FOLDER_DRAFTS);
                if ((draft.getFlagBitmask() & Flag.BITMASK_SYNC) != 0)
                    alterTag(null, ID_FOLDER_DRAFTS, MailItem.TYPE_FOLDER,
                        Flag.ID_FLAG_SYNC, false);
                if ((draft.getFlagBitmask() & Flag.BITMASK_SYNCFOLDER) != 0)
                    alterTag(null, ID_FOLDER_DRAFTS, MailItem.TYPE_FOLDER,
                        Flag.ID_FLAG_SYNCFOLDER, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getItemFlagString(MailItem mi) {
        if (hasFolders && mi.getType() == MailItem.TYPE_FOLDER) {
            try {
                OfflineDataSource ds = (OfflineDataSource)(OfflineProvisioning.
                    getOfflineInstance().getDataSource(getAccount()));
                if (ds.isSyncInboxOnly()) {
                    int flags = mi.getFlagBitmask();
                    
                    flags &= ~Flag.BITMASK_SYNCFOLDER;
                    flags &= ~Flag.BITMASK_SYNC;
                    return Flag.bitmaskToFlags(flags);
                }
            } catch (ServiceException x) {}
        }
        return mi.getFlagString();
    }

    @Override
    public synchronized void alterTag(OperationContext octxt, int itemId,
        byte type, int tagId, boolean addTag) throws ServiceException {
        if (tagId == Flag.ID_FLAG_SYNC && addTag) {
            Folder folder = getFolderById(itemId);
            if ((folder.getFlagBitmask() & Flag.ID_FLAG_SYNCFOLDER) == 0)
                throw MailServiceException.MODIFY_CONFLICT();
        }
        super.alterTag(octxt, itemId, type, tagId, addTag);
    }

    private boolean isSyncEnabledByDefault(String path) throws ServiceException {
        OfflineDataSource ds = (OfflineDataSource)(OfflineProvisioning.
            getOfflineInstance().getDataSource(getAccount()));
        return ds != null && ds.isSyncEnabledByDefault(path);
    }

    private void alterSyncFolderFlag(Folder folder, boolean canSync)
        throws ServiceException {
        folder.alterTag(mSyncFolderFlag, canSync);
        if (canSync) {
            folder.mData.flags |= mSyncFolderFlag.getBitmask();
            if (isSyncEnabledByDefault(folder.getPath())) {
                folder.alterTag(mSyncFlag, canSync);
                folder.mData.flags |= mSyncFlag.getBitmask();
            }
        } else {
            folder.mData.flags &= ~mSyncFolderFlag.getBitmask();
            folder.mData.flags &= ~mSyncFlag.getBitmask();
        }
    }

    @Override
    void archiveSingleItem(MailItem item, boolean toArchive, boolean isTrashing)
        throws ServiceException {
        super.archiveSingleItem(item, toArchive, isTrashing);
        if (hasFolders && item instanceof Folder)
            alterSyncFolderFlag((Folder)item, !toArchive);
    }

    @Override
    void itemCreated(MailItem item, boolean inArchive) throws ServiceException {
        if (hasFolders && !inArchive && item instanceof Folder &&
            ((Folder)item).getDefaultView() == MailItem.TYPE_MESSAGE &&
            (((Folder)item).getUrl() == null || ((Folder)item).getUrl().equals(""))) {
            alterSyncFolderFlag((Folder)item, true);
            if (isFlat)
                item.alterTag(mNoInferiorsFlag, true);
        }
    }

    @Override
    public MailSender getMailSender() {
        return new OfflineMailSender();
    }

    private boolean isAutoSyncDisabled(DataSource ds) {
        return ds.getSyncFrequency() <= 0;
    }

    @Override
    public boolean isAutoSyncDisabled() {
        try {
            List<DataSource> dataSources = OfflineProvisioning.
                getOfflineInstance().getAllDataSources(getAccount());
            for (DataSource ds : dataSources) {
                if (!isAutoSyncDisabled(ds))
                    return false;
            }
        } catch (ServiceException x) {
            OfflineLog.offline.error(x);
        }
        return true;
    }

    @Override
    protected void syncOnTimer() {
        try {
            sync(false, false);
        } catch (ServiceException x) {
            OfflineLog.offline.error(x);
        }
    }

    private boolean isTimeToSync(DataSource ds) throws ServiceException {
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        if (isAutoSyncDisabled(ds) || !syncMan.reauthOK(ds) || !syncMan.retryOK(ds))
            return false;
        long freqLimit = syncMan.getSyncFrequencyLimit();
        long frequency = ds.getSyncFrequency() < freqLimit ? freqLimit :
            ds.getSyncFrequency();
        return System.currentTimeMillis() - syncMan.getLastSyncTime(
            ds.getName()) >= frequency;
    }

    private void syncAllLocalDataSources(boolean force, boolean isOnRequest) throws
        ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        List<DataSource> dataSources = prov.getAllDataSources(getAccount());
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        for (DataSource ds : dataSources) {
            if (!force && !isOnRequest && !isTimeToSync(ds))
                continue;
            try {
                OfflineLog.offline.info(
                    ">>>>>>>> name=%s;version=%s;build=%s;release=%s;os=%s;type=%s",
                    ds.getAccount().getName(), OfflineLC.zdesktop_version.value(),
                    OfflineLC.zdesktop_buildid.value(), OfflineLC.zdesktop_relabel.value(),
                    System.getProperty("os.name") + " " +
                    System.getProperty("os.arch") + " " +
                    System.getProperty("os.version"), ds.getType());
                syncMan.syncStart(ds.getName());
                importData(ds, isOnRequest);
                syncMan.syncComplete(ds.getName());
                OfflineProvisioning.getOfflineInstance().setDataSourceAttribute(
                    ds, OfflineConstants.A_zimbraDataSourceLastSync,
                    Long.toString(System.currentTimeMillis()));
            } catch (Exception x) {
                if (isDeleting())
                    OfflineLog.offline.info("Mailbox \"%s\" is being deleted",
                        getAccountName());
                else
                    syncMan.processSyncException(ds, x);
            }
        }
    }

    private static void importData(DataSource ds, boolean isOnRequest)
        throws ServiceException {
        // Force a full sync if INBOX sync enabled and has not yet been
        // successfully sync'd
        Folder inbox = ds.getMailbox().getFolderById(Mailbox.ID_FOLDER_INBOX);
        boolean forceSync = ds.isSyncEnabled(inbox) && !ds.hasSyncState(inbox.getId());
        boolean fullSync = isOnRequest || forceSync;
        List<Integer> folderIds = null;
        OfflineDataSource ods = (OfflineDataSource)ds;
        
        if (!fullSync && ods.isEmail()) {
            // Import only INBOX and SENT (if not save-to-sent) folders
            folderIds = new ArrayList<Integer>(2);
            folderIds.add(Mailbox.ID_FOLDER_INBOX);
            if (!ds.isSaveToSent()) {
                folderIds.add(Mailbox.ID_FOLDER_SENT);
            }
        }
        DataSourceManager.importData(ds, folderIds, fullSync);
    }

    public void sync(boolean isOnRequest, boolean isDebugTraceOn) throws ServiceException {
        if (lockMailboxToSync()) {
            synchronized (syncLock) {
                if (isOnRequest && isDebugTraceOn) {
                    OfflineLog.offline.debug(
                        "============================== SYNC DEBUG TRACE START ==============================");
                    getOfflineAccount().setRequestScopeDebugTraceOn(true);
                }
                try {
                    syncAllLocalDataSources(false, isOnRequest);
                } catch (Exception x) {
                    if (isDeleting())
                        OfflineLog.offline.info("Mailbox \"%s\" is being deleted",
                            getAccountName());
                    else
                        OfflineLog.offline.error(
                            "exception encountered during sync", x);
                } finally {
                    if (isOnRequest && isDebugTraceOn) {
                        getOfflineAccount().setRequestScopeDebugTraceOn(false);
                        OfflineLog.offline.debug(
                            "============================== SYNC DEBUG TRACE END ================================");
                    }
                    unlockMailbox();
                }
            }
        } else if (isOnRequest) {
            OfflineLog.offline.debug("sync already in progress");
        }
    }
    
    Set<Folder> getAccessibleFolders(short rights) throws ServiceException {
        Set<Folder> accessable = super.getAccessibleFolders(rights);
        boolean all = true;
        OfflineDataSource ds = (OfflineDataSource)(OfflineProvisioning.
            getOfflineInstance().getDataSource(getAccount()));
        Set<Folder> visible = new HashSet<Folder>();

        if (ds == null)
            return accessable;
        for (Folder folder : accessable == null ? getFolderById(
            ID_FOLDER_ROOT).getSubfolderHierarchy() : accessable) {
            if (folder.getId() > Mailbox.FIRST_USER_ID ||
                folder.getId() == ID_FOLDER_FAILURE ||
                folder.getId() == ID_FOLDER_OUTBOX ||
                folder.getDefaultView() != MailItem.TYPE_MESSAGE ||
                ds.isSyncCapable(folder))
                visible.add(folder);
            else
                all = false;
        }
        return all ? null : visible;
    }
}
