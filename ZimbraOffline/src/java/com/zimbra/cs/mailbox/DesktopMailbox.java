package com.zimbra.cs.mailbox;

import java.util.Timer;
import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.util.Zimbra;

public abstract class DesktopMailbox extends Mailbox {

	private Timer timer;
	private TimerTask currentTask;
	
	public DesktopMailbox(MailboxData data) throws ServiceException {
		super(data);
		resetSyncTimer();
	}
	
	@Override
    public synchronized void deleteMailbox() throws ServiceException {
		cancelCurrentTask();
		super.deleteMailbox();
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
	
	protected synchronized void resetSyncTimer() throws ServiceException {
		if (((OfflineAccount)getAccount()).isLocalAccount())
			return;
		
		cancelCurrentTask();
		
//		if (isAutoSyncDisabled())
//			return;
		
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
		
		timer = timer == null ? new Timer("sync-timer-" + getAccount().getName()) : timer;
		timer.schedule(currentTask, 5 * Constants.MILLIS_PER_SECOND, 5 * Constants.MILLIS_PER_SECOND);
	}
	
	protected synchronized void syncNow() {
		//do nothing
	}
	
	protected abstract void syncOnTimer();

	public abstract boolean isAutoSyncDisabled();
}
