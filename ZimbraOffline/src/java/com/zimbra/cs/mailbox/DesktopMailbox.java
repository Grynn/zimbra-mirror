/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009 Zimbra, Inc.
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

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbMailbox;
import com.zimbra.cs.db.DbOfflineMailbox;
import com.zimbra.cs.mailbox.util.TypedIdList;
import com.zimbra.cs.mailbox.OfflineMailbox.OfflineContext;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.redolog.op.CreateFolder;
import com.zimbra.cs.session.PendingModifications;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.cs.util.ZimbraApplication;

public abstract class DesktopMailbox extends Mailbox {

	static class DeletingMailbox extends DesktopMailbox {
		DeletingMailbox(MailboxData data) throws ServiceException {
			super(data);
		}
		
		@Override synchronized boolean finishInitialization() {
	        final String accountId = getAccountId();
	        new Thread(new Runnable() {
				public void run() {
					try {
						deleteThisMailbox();
					} catch (Exception x) {
						OfflineLog.offline.error("Deleting mailbox %s mailbox", accountId, x);
					}
				}
	        }, "mailbox-reaper:" + accountId).start();
			return false;
		}
		
		@Override void resetSyncStatus() {}
		
		@Override public boolean isAutoSyncDisabled() { return false; }
		
		@Override protected void syncOnTimer() {}
	}
	
	private static final String DELETING_MID_SUFFIX = ":delete";
	
	static DesktopMailbox newMailbox(MailboxData data) throws ServiceException {
    	if (data.accountId.endsWith(DELETING_MID_SUFFIX))
    		return new DeletingMailbox(data);
    	
    	OfflineAccount account = (OfflineAccount)Provisioning.getInstance().get(AccountBy.id, data.accountId);
    	if (account == null)
    		throw AccountServiceException.NO_SUCH_ACCOUNT(data.accountId);
    	
    	if (account.isSyncAccount()) {
    		return new OfflineMailbox(data);
    	}
        return new LocalMailbox(data);
	}

	public static final String OUTBOX_PATH = "Outbox";
	public static final String ARCHIVE_PATH = "Local Folders";
	public static final String FAILURE_PATH = "Error Reports";
	public static final int ID_FOLDER_FAILURE = 252;
    public static final int ID_FOLDER_ARCHIVE = 253;
    public static final int ID_FOLDER_OUTBOX = 254;
	
    private static final String CONFIG_OFFLINE_VERSION = "offline_ver";
    
    private OfflineMailboxVersion offlineVersion;
    private boolean isOfflineVerCheckComplete;
    
	private Timer timer;
	private TimerTask currentTask;
	
	private boolean isDeleting;
	
	private String accountName;
	
	public DesktopMailbox(MailboxData data) throws ServiceException {
		super(data);
		
		if (this instanceof DeletingMailbox)
			accountName = getAccountId();
		else {
		    OfflineAccount account = (OfflineAccount)getAccount();
		    if (account.isDataSourceAccount())
			    accountName = account.getAttr(OfflineProvisioning.A_offlineDataSourceName);
		    else
			    accountName = account.getName();
	    }
	}
	
    public OfflineAccount getOfflineAccount() throws ServiceException {
    	return (OfflineAccount)getAccount();
    }
	
    @Override protected synchronized void initialize() throws ServiceException {
        super.initialize();
        // create a system folders
        Folder userRoot = getFolderById(ID_FOLDER_USER_ROOT);
        Folder.create(ID_FOLDER_OUTBOX, this, userRoot, OUTBOX_PATH, Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR, null, null);
        Folder.create(ID_FOLDER_FAILURE, this, userRoot, FAILURE_PATH, Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR, null, null);
        
        // set the version to CURRENT
        Metadata md = new Metadata();
        offlineVersion = OfflineMailboxVersion.CURRENT();
        offlineVersion.writeToMetadata(md);
        DbMailbox.updateConfig(this, CONFIG_OFFLINE_VERSION, md);
    }
    
	@Override synchronized boolean finishInitialization() throws ServiceException {
		if (super.finishInitialization()) {
			ensureSystemFolderExists();
			checkOfflineVersion();
			initSyncTimer();
			return true;
		}
		return false;
	}
	
	synchronized void checkOfflineVersion() throws ServiceException {
    	if (!isOfflineVerCheckComplete) {
            if (offlineVersion == null) {
                Metadata md = getConfig(null, CONFIG_OFFLINE_VERSION);
                offlineVersion = OfflineMailboxVersion.fromMetadata(md);
            }
    		
            if (!offlineVersion.atLeast(2)) {
                OfflineMailboxMigrationV2.doMigration(this);
                updateOfflineVersion(new OfflineMailboxVersion((short)2));
    		}
    		
            isOfflineVerCheckComplete = true;
    	}
	}

    private synchronized void updateOfflineVersion(OfflineMailboxVersion ver) throws ServiceException {
    	offlineVersion = ver;
        Metadata md = getConfig(null, CONFIG_OFFLINE_VERSION);
        if (md == null)
            md = new Metadata();
        offlineVersion.writeToMetadata(md);
        setConfig(null, CONFIG_OFFLINE_VERSION, md);
    }
	
	synchronized void ensureSystemFolderExists() throws ServiceException {
		Folder f = null;
		try {
			f = getFolderById(ID_FOLDER_FAILURE);
		} catch (MailServiceException.NoSuchItemException x) {}
		if (f == null) {
	        CreateFolder redo = new CreateFolder(getId(), FAILURE_PATH, ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR, null);
	        redo.setFolderId(ID_FOLDER_FAILURE);
	        redo.start(System.currentTimeMillis());
            createFolder(new OfflineContext(redo), FAILURE_PATH, ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR, null);
		}
	}
	
    Object syncLock = new Object();
    
    private boolean mSyncRunning;
    
    boolean lockMailboxToSync() {
    	if (isDeleting() || !OfflineSyncManager.getInstance().isServiceOpen() || OfflineSyncManager.getInstance().isUiLoadingInProgress())
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

			beginMaintenance(); //putting mailbox in maintenance will cause sync to stop when writing
		}
		
		synchronized (syncLock) { //wait for any hot sync thread to unwind
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
		        mm.markMailboxDeleted(this); //to remove from cache
        	}
	        mm.getMailboxById(getId(), true); //the mailbox will now be loaded as a DeletingMailbox
        } else {
        	deleteThisMailbox();
        }
    }
	
	void resetSyncStatus() throws ServiceException {
		OfflineSyncManager.getInstance().resetStatus(accountName);
		((OfflineAccount)getAccount()).resetLastSyncTimestamp();
                OfflineYAuth.deleteRawAuthManager(this);
        }
	
	private synchronized String unhookMailboxForDeletion() throws ServiceException {
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
	
	@Override
    public synchronized void alterTag(OperationContext octxt, int itemId, byte type, int tagId, boolean addTag) throws ServiceException {
        if (tagId == Flag.ID_FLAG_SYNC && addTag) {
        	Folder folder = getFolderById(itemId);
        	if ((folder.getFlagBitmask() & Flag.ID_FLAG_SYNCFOLDER) == 0)
        		throw MailServiceException.MODIFY_CONFLICT();
        }
        super.alterTag(octxt, itemId, type, tagId, addTag);
    }
	
	private synchronized void cancelCurrentTask() {
		if (currentTask != null)
			currentTask.cancel();
		currentTask = null;
	}
	
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
        	syncNow();
        }
    }
    
    private void alterArchivedFlag(MailItem item, boolean toArchive) throws ServiceException {
    	// alter \Archived flag, but don't use MailItem.alterSystemFlag() since that would insert more changes into PendingModifications
    	// we are currently looping through.  in any case we don't need to keep track of this particular flag change.
        Flag archivedFlag = getFlagById(Flag.ID_FLAG_ARCHIVED);
    	DbMailItem.alterTag(archivedFlag, Arrays.asList(item.getId()), toArchive);
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
    private void archive(MailItem item, boolean toArchive, boolean isTrashing) throws ServiceException {
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
    
    void archiveSingleItem(MailItem item, boolean toArchive, boolean isTrashing) throws ServiceException {
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

	@SuppressWarnings("unused")
    void trackChangeNew(MailItem item) throws ServiceException {}

	@SuppressWarnings("unused")
    void trackChangeModified(MailItem item, int changeMask) throws ServiceException {}

	@SuppressWarnings("unused")
    boolean trackChangeArchived(MailItem item, boolean toArchive, boolean isTrashing) throws ServiceException { return true; }

	@SuppressWarnings("unused")
    void itemCreated(MailItem item, boolean inArchive) throws ServiceException {}

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
					} catch (Throwable e) { //don't let exceptions kill the timer
						if (e instanceof OutOfMemoryError)
							Zimbra.halt("Caught out of memory error", e);
						OfflineLog.offline.warn("Caught exception in timer ", e);
					}
				}
			};
		
		timer = new Timer("mid=" + getId());
		timer.schedule(currentTask, 10 * Constants.MILLIS_PER_SECOND, 5 * Constants.MILLIS_PER_SECOND);
	}
	
	protected synchronized void syncNow() {
		//do nothing
	}
	
	protected abstract void syncOnTimer();

	public abstract boolean isAutoSyncDisabled();
}
