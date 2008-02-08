package com.zimbra.cs.mailbox;

import java.util.Timer;
import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.util.Zimbra;

public abstract class DesktopMailbox extends Mailbox {

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
	
	@Override
	synchronized boolean finishInitialization() throws ServiceException {
		if (super.finishInitialization()) {
			initSyncTimer();
			return true;
		}
		return false;
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
