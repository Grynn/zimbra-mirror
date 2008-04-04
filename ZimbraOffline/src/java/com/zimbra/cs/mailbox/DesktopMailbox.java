package com.zimbra.cs.mailbox;

import java.util.Timer;
import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.OfflineMailbox.OfflineContext;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.redolog.op.CreateFolder;
import com.zimbra.cs.util.Zimbra;

public abstract class DesktopMailbox extends Mailbox {

	public static final String OUTBOX_PATH = "Outbox";
	public static final String FAILURE_PATH = "Sync Failures";
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
	
	protected synchronized void initSyncTimer() throws ServiceException {
		if (((OfflineAccount)getAccount()).isLocalAccount())
			return;
		
		cancelCurrentTask();
		
		currentTask = new TimerTask() {
				public void run() {
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
