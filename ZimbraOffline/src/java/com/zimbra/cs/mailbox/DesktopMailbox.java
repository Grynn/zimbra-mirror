package com.zimbra.cs.mailbox;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.mailbox.OfflineMailbox.OfflineContext;
import com.zimbra.cs.mailbox.util.TypedIdList;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.util.OfflineYAuth;
import com.zimbra.cs.redolog.op.CreateFolder;
import com.zimbra.cs.session.PendingModifications;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.util.Zimbra;
import com.zimbra.cs.util.ZimbraApplication;

public abstract class DesktopMailbox extends Mailbox {

	public static final String OUTBOX_PATH = "Outbox";
	public static final String ARCHIVE_PATH = "Archive";
	public static final String FAILURE_PATH = "Error Reports";
	public static final int ID_FOLDER_FAILURE = 252;
    public static final int ID_FOLDER_ARCHIVE = 253;
    public static final int ID_FOLDER_OUTBOX = 254;
	
	private Timer timer;
	private TimerTask currentTask;
	
	private boolean isDeleting;
	
	private String accountName;
	
	public DesktopMailbox(MailboxData data) throws ServiceException {
		super(data);
		
		OfflineAccount account = (OfflineAccount)getAccount();
		if (account.isDataSourceAccount())
			accountName = account.getAttr(OfflineProvisioning.A_offlineDataSourceName);
		else
			accountName = account.getName();
	}
	
    @Override protected synchronized void initialize() throws ServiceException {
        super.initialize();

        // create a system outbox folder
        Folder userRoot = getFolderById(ID_FOLDER_USER_ROOT);
        Folder.create(ID_FOLDER_OUTBOX, this, userRoot, OUTBOX_PATH, Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR, null);
        Folder.create(ID_FOLDER_ARCHIVE, this, userRoot, ARCHIVE_PATH, Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, Flag.BITMASK_ARCHIVED, MailItem.DEFAULT_COLOR, null);
        Folder.create(ID_FOLDER_FAILURE, this, userRoot, FAILURE_PATH, Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR, null);
    }
    
	@Override
	synchronized boolean finishInitialization() throws ServiceException {
		if (super.finishInitialization()) {
			initSyncTimer();
			return true;
		}
		return false;
	}
	
	synchronized void ensureFailureFolderExists() throws ServiceException {
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
	
	public boolean isDeleting() {
		return isDeleting;
	}
	
	public String getAccountName() {
		return accountName;
	}

	@Override
        public synchronized void deleteMailbox() throws ServiceException {
		isDeleting = true;
		cancelCurrentTask();
		super.deleteMailbox();
		OfflineSyncManager.getInstance().resetStatus(accountName);
		((OfflineAccount)getAccount()).resetLastSyncTimestamp();
                OfflineYAuth.deleteRawAuthManager(this);
        }
	
	@Override
    public synchronized void alterTag(OperationContext octxt, int itemId, byte type, int tagId, boolean addTag) throws ServiceException {
        if (tagId == Flag.ID_FLAG_SYNC) {
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
                    boolean isInArchive = isInArchive(item.getPath());
                	if (!isInArchive || !item.isTagged(mArchivedFlag)) { //either not in archive, or newly archived, we need to keep track
                		trackChangeModified(item, change.why);
                        if (item.getFolderId() == ID_FOLDER_OUTBOX)
                        	outboxed = true;
                	}
                    
                	if ((change.why & Change.MODIFIED_FOLDER) != 0) {
                    	if (isInArchive && !item.isTagged(mArchivedFlag)) //moved into archive
                        	archive(item, true);
                    	else if (!isInArchive && item.isTagged(mArchivedFlag)) //moved out of archive
                        	archive(item, false);
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
    	//alter \Archived flag, but don't use MailItem.alterSystemFlag() since that would insert more changes into PendingModifications
    	//we are currently looping through.  in any case we don't need to keep track of this particular flag change.
    	DbMailItem.alterTag(mArchivedFlag, Arrays.asList(item.getId()), toArchive);
    	if (toArchive)
    		item.mData.flags |= mArchivedFlag.getBitmask();
        else
        	item.mData.flags &= ~mArchivedFlag.getBitmask();
    }

    /**
     * An item has been moved into or out of archive.  We'll set or clear \Archive flag, and will set or clear dirty bits accordingly.
     * If the item is a folder, we do the same to all its subfolders and leaf items.
     * 
     * @param item
     * @param toArchive true to move into archive; false to move out of
     * @throws ServiceException
     */
    private void archive(MailItem item, boolean toArchive) throws ServiceException {
    	if (item instanceof Folder) {
    		TypedIdList ids = DbMailItem.listByFolder((Folder)item, true);
    		for (byte type : ids.types()) {    			
    			MailItem[] items = getItemById(ids.getIds(type), type);
    			for (MailItem i : items) {
    				if (type == MailItem.TYPE_FOLDER)
    					archive(i, toArchive);
    				else
    					archiveSingleItem(i, toArchive);
    			}
    		}
    	}
    	archiveSingleItem(item, toArchive);
    }
    
    void archiveSingleItem(MailItem item, boolean toArchive) throws ServiceException {
    	alterArchivedFlag(item, toArchive);
    	trackChangeArchived(item, toArchive);
    }
    
    public static boolean isInArchive(String path) {
    	return path.startsWith("/" + ARCHIVE_PATH);
    }
    
    boolean isItemInArchive(MailItem item) throws ServiceException {
    	return (item.getInternalFlagBitmask() & Flag.BITMASK_ARCHIVED) != 0;
    }
	
	void trackChangeNew(MailItem item) throws ServiceException {}
	
	void trackChangeModified(MailItem item, int changeMask) throws ServiceException {}
	
	void trackChangeArchived(MailItem item, boolean toArchive) throws ServiceException {}
	
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
		
		timer = new Timer("sync-timer-" + getAccount().getName());
		timer.schedule(currentTask, 5 * Constants.MILLIS_PER_SECOND, 5 * Constants.MILLIS_PER_SECOND);
	}
	
	protected synchronized void syncNow() {
		//do nothing
	}
	
	protected abstract void syncOnTimer();

	public abstract boolean isAutoSyncDisabled();
}
